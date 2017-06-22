import java.util.*;

public class Main {

    public static void main(String[] args) {
//        List<World> worlds = new ArrayList<>();
//        char[] map = {'.', '.', '0', '0', '.', '.', '.', '.', '0'};
        String mapString = "...........................0.........0.......0......0...0...0...0..0...0...0..0...0...0...0......0.......0.........0....0.....0........0.0.....";
        int width = 13;
        int height = 11;
        Owner robot = new Owner(0, 0, new ArrayList<FootPrint>());
//        World initWorld = new World(3, 3, 0, map, new ArrayList<Bomb>(), new Owner(0, 0, new ArrayList<FootPrint>()));
//        worlds.add(initWorld);
//
//        int bombNumLimit = 1;
//
//        long startTime = System.currentTimeMillis();
//
//        for (int depth = 1; depth < 6; depth++) {
//            List<World> removeList = new ArrayList<>();
//            List<World> addList = new ArrayList<>();
//            for (World oldWorld : worlds) {
//                for (Action action : Action.values()) {
//                    World newWorld = oldWorld.clone();
//                    if (newWorld.perform(action)) {
//                        addList.add(newWorld);
//                    }
//                }
//                if (oldWorld.owner.myBombs.size() < bombNumLimit) {
//                    for (Action action : Action.values()) {
//                        World newWorld = oldWorld.clone();
//                        if (newWorld.bombAndPerform(action)) {
//                            addList.add(newWorld);
//                        }
//                    }
//                }
//                removeList.add(oldWorld);
//            }
//            worlds.removeAll(removeList);
//            worlds.addAll(addList);
//
//            Collections.sort(worlds, new Comparator<World>() {
//                @Override
//                public int compare(World worldA, World worldB) {
//                    return worldB.score - worldA.score;
//                }
//            });
//
//            if (worlds.size() > 400) {
//                worlds = worlds.subList(0, 400);
//            }
//
//            // Stop to simulate the next depth before timeout
//            long timeDiff = System.currentTimeMillis() - startTime;
//            System.out.println("time used " + timeDiff + " after depth " + depth);
//            if (timeDiff > 90) {
//                break;
//            }
//        }
//
//        FootPrint output = worlds.get(0).owner.getFootPrints().get(0);
//        System.out.println("x:" + output.x + ", y:" + output.y + ", bomb:" + output.bombed);
//        for (FootPrint footPrint : worlds.get(0).owner.getFootPrints()) {
//            System.out.println("step to " + footPrint.x + "," + footPrint.y + " " + footPrint.bombed);
//        }
//        Scanner in = new Scanner(System.in);
//        int width = in.nextInt();
//        int height = in.nextInt();
//        int myId = in.nextInt();
        List<World> worlds = new ArrayList<>();
        List<Bomb> bombList = new ArrayList<>();
        long startTime, lastEndTime = 0, endTime = 0;
        String outTime = "";
        long timeLimit = 80;
        World initWorld = null;
        // game loop
        while (true) {
            startTime = System.currentTimeMillis();
            if (initWorld == null) {
                initWorld = new World(width, height, 0, mapString.toCharArray(), bombList, robot);
            } else {
                initWorld = worlds.get(0);
            }
            worlds.clear();
            bombList.clear();
//            Owner robot = new Owner(0, 0, new ArrayList<FootPrint>());
//            String mapString = "";
//
//            for (int i = 0; i < height; i++) {
//                mapString += in.next();
//            }
//            int entities = in.nextInt();
//            for (int i = 0; i < entities; i++) {
//                int entityType = in.nextInt();
//                int owner = in.nextInt();
//                int x = in.nextInt();
//                int y = in.nextInt();
//                int param1 = in.nextInt();
//                int param2 = in.nextInt();
//                //System.err.println("owner:"+owner+", x:"+x+", y:"+y+", param1:"+param1+", param2:"+param2);
//
//                if (entityType == 1) {
//                    Bomb newBomb = Bomb.create(x, y, param1);
//                    bombList.add(newBomb);
//                    if (owner == myId) {
//                        robot.myBombs.add(newBomb);
//                    }
//
//                } else if (entityType == 0 && owner == myId) {
//                    robot.x = x;
//                    robot.y = y;
//                }
//            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            //System.out.println("BOMB 6 5");


//            World initWorld = new World(width, height, 0, mapString.toCharArray(), bombList, robot);
            worlds.add(initWorld);
            int bombNumLimit = 1;
            boolean stop = false;
            for (int depth = 1; ; depth++) {
//                if (stop) {
//                    break;
//                }
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

//                    // Stop to simulate the next depth before timeout
//                    long timeDiff = System.currentTimeMillis() - startTime;
//                    //System.err.println("time used " + timeDiff + " after depth " + depth);
//                    if (timeDiff > timeLimit) {
//                        stop = true;
//                        break;
//                    }
                }
                worlds.removeAll(removeList);
                worlds.addAll(addList);

                Collections.sort(worlds, new Comparator<World>() {
                    @Override
                    public int compare(World worldA, World worldB) {
                        return worldB.score - worldA.score;
                    }
                });

                System.err.println("size " + worlds.size());
                if (worlds.size() > 400) {
                    worlds = worlds.subList(0, 400);
                }


                // Stop to simulate the next depth before timeout
                long timeDiff = System.currentTimeMillis() - startTime;
                System.err.println("time used " + timeDiff + " after depth " + depth + ", size " + worlds.size());
                if (timeDiff > timeLimit) {
                    break;
                }
            }

            FootPrint output = worlds.get(0).owner.getFootPrints().get(0);
            String command = (output.bombed ? "BOMB" : "MOVE") + " " + output.x + " " + output.y + " @@";
            endTime = System.currentTimeMillis() - lastEndTime;
            lastEndTime = System.currentTimeMillis();
            outTime += " " + endTime;
            System.err.println(worlds.size() + " " + command + outTime);
            System.out.println(command);
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
        Bomb bomb = Bomb.create(x, y, 8);
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

    Bomb(int x, int y, int countDown) {
        super(x, y);
        this.countDown = countDown;
    }

    public static Bomb create(int x, int y, int countDown) {
        Bomb bomb = new Bomb(x, y, countDown);
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