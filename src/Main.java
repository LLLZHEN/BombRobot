import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<World> worlds = new ArrayList<>();
        char[] map = {'.', '.', '.', '.', '.', '.', '.', '.', '0'};
        World initWorld = new World(3, 3, 0, map, new ArrayList<Bomb>(), new Owner(2, 0, new ArrayList<Entity>()));
        worlds.add(initWorld);

        long startTime = System.currentTimeMillis();

        for (int depth = 1; depth < 2; depth++) {
            List<World> removeList = new ArrayList<>();
            List<World> addList = new ArrayList<>();
            for (World oldWorld : worlds) {
                for (Action action : Action.values()) {
                    World newWorld = oldWorld.clone();
                    if (newWorld.perform(action)) {
                        addList.add(newWorld);
                    }
                }
                removeList.add(oldWorld);
            }
            worlds.removeAll(removeList);
            worlds.addAll(addList);

            Collections.sort(worlds, new Comparator<World>() {
                @Override
                public int compare(World worldA, World worldB) {
                    return worldA.score - worldB.score;
                }
            });

            if (worlds.size() > 400) {
                worlds = worlds.subList(0, 400);
            }

            // Stop to simulate the next depth before timeout
            if (System.currentTimeMillis() - startTime > 90) {
                break;
            }
        }

        Entity output = worlds.get(0).owner.getFootPrints().get(0);
        System.out.println("x:" + output.x + ", y:" + output.y);
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
        char[] newMap = map.clone();
        List<Bomb> newBombs = new ArrayList<>(bombs);
        return new World(width, height, score, newMap, newBombs, owner.clone());
    }

    public boolean perform(Action action) {
        switch (action) {
            case LEFT:
                owner.moveLeft();
                break;
//            case BOMB_LEFT:
//                owner.moveLeft();
//                break;

            default:
                return false;
        }

        return update();
    }

    private boolean update() {
        // Check invalid movement
        if (isOutsideMap(owner) || isCollidedWithBox(owner) || isCollidedWithBomb(owner)) {
            return false;
        }

        // Check explosion
        List<Bomb> bang = new ArrayList<>();
        for (Bomb bomb : bombs) {
            bomb.update();
            if (bomb.getCountDown() == 0) {
                bang.add(bomb);
            }
        }
        bombs.removeAll(bang);
        for (Bomb bomb : bang) {

        }

//        for (Entity fire : allFires) {
//            if (owner.isCollided(fire)) {
//                // killed
//                return false;
//            }
//
//            if (isCollidedWithBox(fire)) {
//                clearBox(fire);
//                score++;
//            }
//        }

        return true;
    }

    private boolean isOutsideMap(Entity entity) {
        return entity.x < 0 || entity.x >= width || entity.y < 0 || entity.y >= height;
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
    BOMB_STAY
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

class Owner extends Entity implements Cloneable {
    private List<Entity> footPrints;

    Owner(int x, int y, List<Entity> footPrints) {
        super(x, y);
        this.footPrints = footPrints;
    }

    public void moveLeft() {
        x--;
        footPrints.add(new Entity(x, y));
    }

    public void moveRight() {
        x++;
        footPrints.add(new Entity(x, y));
    }

    public void moveUp() {
        y--;
        footPrints.add(new Entity(x, y));
    }

    public void moveDown() {
        y++;
        footPrints.add(new Entity(x, y));
    }

    public List<Entity> getFootPrints() {
        return footPrints;
    }

    @Override
    protected Owner clone() {
        List<Entity> newFootPrints = new ArrayList<>(footPrints);
        return new Owner(x, y, newFootPrints);
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

    public int getCountDown() {
        return countDown;
    }

    public int getPower() {
        return power;
    }

    public void update() {
        countDown--;
    }
}