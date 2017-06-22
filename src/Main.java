import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<World> worlds = new ArrayList<>();
        char[] map = {'.', '.', '0', '0', '.', '.', '.', '.', '0'};
        World initWorld = new World(3, 3, 0, map, new ArrayList<Bomb>(), new Owner(0, 0, new ArrayList<FootPrint>()));
        worlds.add(initWorld);

        int bombNumLimit = 1;

        long startTime = System.currentTimeMillis();

        for (int depth = 1; depth < 6; depth++) {
            List<World> removeList = new ArrayList<>();
            List<World> addList = new ArrayList<>();
            for (World oldWorld : worlds) {
                for (Action action : Action.values()) {
                    World newWorld = oldWorld.clone();
                    if (newWorld.perform(action)) {
                        addList.add(newWorld);
                    }
                }
                if (oldWorld.owner.myBombs.size() < bombNumLimit) {
                    for (Action action : Action.values()) {
                        World newWorld = oldWorld.clone();
                        if (newWorld.bombAndPerform(action)) {
                            addList.add(newWorld);
                        }
                    }
                }
                removeList.add(oldWorld);
            }
            worlds.removeAll(removeList);
            worlds.addAll(addList);

            Collections.sort(worlds, new Comparator<World>() {
                @Override
                public int compare(World worldA, World worldB) {
                    return worldB.score - worldA.score;
                }
            });

            if (worlds.size() > 400) {
                worlds = worlds.subList(0, 400);
            }

            // Stop to simulate the next depth before timeout
            long timeDiff = System.currentTimeMillis() - startTime;
            System.out.println("time used " + timeDiff + " after depth " + depth);
            if (timeDiff > 90) {
                break;
            }
        }

        FootPrint output = worlds.get(0).owner.getFootPrints().get(0);
        System.out.println("x:" + output.x + ", y:" + output.y + ", bomb:" + output.bombed);
        for (FootPrint footPrint : worlds.get(0).owner.getFootPrints()) {
            System.out.println("step to " + footPrint.x + "," + footPrint.y + " " + footPrint.bombed);
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
        char[] newMap = map.clone();
        List<Bomb> newBombs = new ArrayList<>(bombs);
        return new World(width, height, score, newMap, newBombs, owner.clone());
    }

    public boolean bombAndPerform(Action action) {
        bombs.add(owner.bomb());
        moveOwner(action);
        owner.saveFootPrint(true);
        return update();
    }

    public boolean perform(Action action) {
        moveOwner(action);
        owner.saveFootPrint(false);
        return update();
    }

    public void moveOwner(Action action) {
        switch (action) {
            case LEFT:
                owner.moveLeft();
                break;
            case RIGHT:
                owner.moveRight();
                break;
            case UP:
                owner.moveUp();
                break;
            case DOWN:
                owner.moveDown();
                break;

            default:
                break;
        }
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
                if (bomb.isMyBomb) {
                    owner.myBombs.remove(bomb);
                }
            }
        }
        bombs.removeAll(bang);
        // Power=3
        int bombPower = 2;
        List<Fire> hitBoxFires = new ArrayList<>();
        List<Fire> removeFires = new ArrayList<>();
        for (int i = 1; i < bombPower; i++) {
            hitBoxFires.clear();
            for (Bomb bomb : bang) {
                removeFires.clear();
                for (Fire fire : bomb.getFires()) {
                    fire.forward();
                    if (isOutsideMap(fire)) {
                        removeFires.add(fire);
                    } else if (isCollidedWithBox(fire)) {
                        removeFires.add(fire);
                        hitBoxFires.add(fire);
                        if (bomb.isMyBomb) {
                            score++;
                        }
                    }
                }
                if (!removeFires.isEmpty()) {
                    bomb.getFires().removeAll(removeFires);
                }
            }
            for (Fire fire : hitBoxFires) {
                clearBox(fire);
            }
        }

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
    RIGHT,
    UP,
    DOWN,
    STAY,
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

class FootPrint {
    int x, y;
    boolean bombed;

    FootPrint(int x, int y, boolean bombed) {
        this.x = x;
        this.y = y;
        this.bombed = bombed;
    }
}

class Owner extends Entity implements Cloneable {
    private List<FootPrint> footPrints;
    public List<Bomb> myBombs = new ArrayList<>();

    Owner(int x, int y, List<FootPrint> footPrints) {
        super(x, y);
        this.footPrints = footPrints;
    }

    public Bomb bomb() {
        Bomb bomb = Bomb.create(x, y);
        bomb.isMyBomb = true;
        myBombs.add(bomb);
        return bomb;
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

    public void saveFootPrint(boolean bombed) {
        footPrints.add(new FootPrint(x, y, bombed));
    }

    public List<FootPrint> getFootPrints() {
        return footPrints;
    }

    @Override
    protected Owner clone() {
        List<FootPrint> newFootPrints = new ArrayList<>(footPrints);
        return new Owner(x, y, newFootPrints);
    }
}

class Bomb extends Entity {
    private int countDown, power;
    private List<Fire> fires;
    public boolean isMyBomb;

    Bomb(int x, int y) {
        super(x, y);
    }

    public static Bomb create(int x, int y) {
        Bomb bomb = new Bomb(x, y);
        bomb.countDown = 1;
        bomb.power = 1;
        return bomb;
    }

    public int getCountDown() {
        return countDown;
    }

    public int getPower() {
        return power;
    }

    public List<Fire> getFires() {
        return fires;
    }

    public void update() {
        countDown--;
        if (countDown == 0) {
            fires = new ArrayList<>();
            fires.add(new Fire(x, y, 0));
            fires.add(new Fire(x, y, 1));
            fires.add(new Fire(x, y, 2));
            fires.add(new Fire(x, y, 3));
        }
    }
}

class Fire extends Entity {
    public boolean isActived = true;
    private int direction; // 0=N, 1=E, 2=S, 3=W

    Fire(int x, int y, int direction) {
        super(x, y);
        this.direction = direction;
    }

    public void forward() {
        if (direction == 0) {
            y--;
        } else if (direction == 1) {
            x++;
        } else if (direction == 2) {
            y++;
        } else if (direction == 3) {
            x--;
        }
    }
}