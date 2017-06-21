import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        List<World> worlds = new ArrayList<>();
        char[] map = {'0', '0'};
        World initWorld = new World(1, 1, 0, map, new ArrayList<Bomb>(), new Owner(1, 1));
        worlds.add(initWorld);

        Iterator<World> worldIterator = worlds.iterator();


        for (int depth = 1; depth < 6; depth++) {
            while (worldIterator.hasNext()) {
                World oldWorld = worldIterator.next();
                for (Action action : Action.values()) {
                    World newWorld = oldWorld.clone();
                    if (newWorld.perform(action)) {
                        worlds.add(newWorld);
                    }
                }
                worldIterator.remove();
            }
        }
    }
}


class World implements Cloneable {
    int width, height;
    char[] map;
    List<Bomb> bombs;
    Owner owner;
    int score;
    public World(int width, int height, int score, char[] map, List<Bomb> bombs, Owner owner) {
        this.width = width;
        this.height = height;
        this.map = map;
        this.bombs = bombs;
        this.owner = owner;
        this.score = score;
    }

    @Override
    public World clone() {
        return new World(width, height, score, map, bombs, owner);
    }

    public boolean perform(Action action) {
        switch (action) {
            case LEFT:
                owner.moveLeft();
                break;

            default:
                break;
        }


        return update();
    }

    private boolean update() {
        // Check invalid movement
        if (isCollidedWithBox(owner) || isCollidedWithBomb(owner)) {
            return false;
        }

        // Check explosion
        List<Entity> allFires = new ArrayList<>();
        for (Bomb bomb : bombs) {
            List<Entity> fires = bomb.update();
            if (fires != null) {
                allFires.addAll(fires);
            }
        }
        for (Entity fire : allFires) {
            if (owner.isCollided(fire)) {
                // killed
                return false;
            }

            if (isCollidedWithBox(fire)) {
                clearBox(fire);
                score++;
            }
        }

        return true;
    }

    private boolean isCollidedWithBox(Entity entity) {
        int offset = entity.x + entity.y * width;
        return map[offset] == '0';
    }

    private void clearBox(Entity entity) {
        int offset = entity.x + entity.y * width;
        map[offset] = '.';
    }

    private boolean isCollidedWithBomb(Entity owner) {
        for (Bomb bomb : bombs) {
            if (owner.isCollided(bomb)) {
                return true;
            }
        }
        return false;
    }
}

enum Action {
    LEFT,
    BOMB_LEFT,
    RIGHT,
    BOMB_RIGHT,
    UP,
    BOMB_UP,
    DOWN,
    BOMB_DOWN,
    STAY,
    BOMB_STAY;
}

class Entity {
    int x, y;
    Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isCollided(Entity entity) {
        return entity.x == this.x && entity.y == this.y;
    }
}

class Owner extends Entity {
    Owner(int x, int y) {
        super(x, y);
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveUp() {
        y--;
    }

    public void moveDown() {
        y++;
    }
}

class Bomb extends Entity {
    private int countDown, power;
    Bomb(int x, int y) {
        super(x, y);
    }

    public static Bomb create(int x, int y) {
        Bomb bomb = new Bomb(x, y);
        bomb.countDown = 8;
        bomb.power = 3;
        return bomb;
    }

    public List<Entity> update() {
        countDown--;
        if (countDown == 0) {
            List<Entity> fires = new ArrayList<>();
            for (int i = 0; i < power; i++) {
                if (i == 0) {
                    fires.add(new Entity(x, y));
                } else {
                    fires.add(new Entity(x - i, y));
                    fires.add(new Entity(x + i, y));
                    fires.add(new Entity(x, y + i));
                    fires.add(new Entity(x, y - i));
                }
            }
            return fires;
        }
        return null;
    }
}