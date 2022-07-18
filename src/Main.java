import game.Game;
import game.engine.Window;

class Main{

    public static void main(String[] args) {
        try {
            int width = Integer.parseInt(args[0]);
            int height = Integer.parseInt(args[1]);
            boolean fullscreen = Boolean.parseBoolean(args[2]);
            int swapInterval = Integer.parseInt(args[3]);
            String resourceRoot;
            try{
                resourceRoot = args[4];
                if(resourceRoot.charAt(resourceRoot.length() - 1) != '/'){
                    resourceRoot = resourceRoot + '/';
                }
            } catch(ArrayIndexOutOfBoundsException e){
                resourceRoot = "res/";
            }
            String title = "Stardew Valley";

            System.out.println("CURRENT DIRECTORY "+ System.getProperty("user.dir"));
            Game game = new Game(
                    new Window(
                            width, height,
                            title,
                            fullscreen,
                            swapInterval,
                            0,0,0
                    ),
                    resourceRoot
            );
            game.run();

        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Please specify the configuration as follows:");
            System.out.println("java -jar programming-project.jar <width> <height> <fullscreen (true/false)> <swapInterval (0 = unlimited, 1 = vsync)>");
            System.exit(-1);
        }

    }
}