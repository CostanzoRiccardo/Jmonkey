package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    /*
    * 0 = meniul de start
    * 1 = a inceput jocul
    * 2 = sfarsit
     */
    private Node optiuniMeniu;
    private Node cutiiClickabile;

    private int fazaJoc = 0;
    private int numarJucatori;
    private boolean turaPlayer1 = true;
    private String varianteAlese[] = {"", "", "", "", "", "", "", "", ""};

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Parte de fundal
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 0.5f));
        getFlyByCamera().setEnabled(false);

        // turn off stats view (you can leave it on, if you want)
        setDisplayStatView(false);
        setDisplayFps(false);

        // Initializam nodurile
        optiuniMeniu = new Node();
        guiNode.attachChild(optiuniMeniu);

        // Cutii invizibile clickabile
        cutiiClickabile = new Node();
        //guiNode.attachChild(cutiiClickabile);

        // Meniul principal
        mainMenu();
        controlMainMenu();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (numarJucatori == 1 && !turaPlayer1) {
            drawXO("o", getGeometryByName("" + miscareAI()).getLocalTranslation().getX() - 50,
                    getGeometryByName("" + miscareAI()).getLocalTranslation().getY() - 50);
            varianteAlese[miscareAI()] = "o";
            turaPlayer1 = !turaPlayer1;
            cutiiClickabile.detachChild(getGeometryByName("" + miscareAI()));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void createTextButton(final String text, final int posX, final int posY, int sizeX, int sizeY) {
        Box b = new Box(sizeX, sizeY, 0);
        Geometry geom = new Geometry(text, b);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        Texture cube1Tex = assetManager.loadTexture(
                "Materials/" + text + ".png");
        mat.setTexture("ColorMap", cube1Tex);
        geom.setMaterial(mat);
        geom.setLocalTranslation(new Vector3f(posX, posY, 0));
        optiuniMeniu.attachChild(geom);
    }

    public void mainMenu() {
        createTextButton("titlu", settings.getWidth() / 2 - 30, settings.getHeight() - 90, 200, 40);
        createTextButton("1", settings.getWidth() / 2 - 30, settings.getHeight() - 180, 100, 30);
        createTextButton("2", settings.getWidth() / 2 - 31, settings.getHeight() - 240, 110, 30);
        createTextButton("quit", settings.getWidth() / 2 - 20, settings.getHeight() - 300, 50, 30);
    }

    public void controlMainMenu() {
        inputManager.addMapping("Click",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Click");
    }

    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Click") && !keyPressed) {
                switch (fazaJoc) {
                    case 0: {
                        // In main menu
                        CollisionResults results = new CollisionResults();
                        Vector3f ori = new Vector3f(inputManager.getCursorPosition().x,
                                inputManager.getCursorPosition().y, 1f);
                        Vector3f dest = new Vector3f(0f, 0f, -1f);
                        Ray ray = new Ray(ori, dest);
                        optiuniMeniu.collideWith(ray, results);
                        if (results.size() > 0) {
                            CollisionResult closest = results.getClosestCollision();
                            //closest.getGeometry().getName();
                            if ("1".equals(closest.getGeometry().getName())) {
                                initInterfataJoc(1);
                            }
                            if ("2".equals(closest.getGeometry().getName())) {
                                initInterfataJoc(2);
                            }
                        }
                        break;
                    }
                    case 1: {
                        // In main menu
                        CollisionResults results = new CollisionResults();
                        Vector3f ori = new Vector3f(inputManager.getCursorPosition().x,
                                inputManager.getCursorPosition().y, 1f);
                        Vector3f dest = new Vector3f(0f, 0f, -1f);
                        Ray ray = new Ray(ori, dest);
                        cutiiClickabile.collideWith(ray, results);
                        if (results.size() > 0) {
                            // Jucam contra AI sau alt jucator
                            CollisionResult closest = results.getClosestCollision();
                            if (turaPlayer1) {
                                drawXO("x", closest.getGeometry().getLocalTranslation().getX() - 50,
                                        closest.getGeometry().getLocalTranslation().getY() - 50);
                                varianteAlese[Integer.parseInt(closest.getGeometry().getName())] = "x";
                            } else {
                                if (numarJucatori == 2) {
                                    drawXO("o", closest.getGeometry().getLocalTranslation().getX() - 50,
                                            closest.getGeometry().getLocalTranslation().getY() - 50);
                                    varianteAlese[Integer.parseInt(closest.getGeometry().getName())] = "o";
                                }
                            }
                            cutiiClickabile.detachChild(closest.getGeometry());
                            turaPlayer1 = !turaPlayer1;
                            if (!checkWin().equals("")) {
                                sfarsitJoc(checkWin());
                            }
                        }
                        break;
                    }
                    case 2: {
                        CollisionResults results = new CollisionResults();
                        Vector3f ori = new Vector3f(inputManager.getCursorPosition().x,
                                inputManager.getCursorPosition().y, 1f);
                        Vector3f dest = new Vector3f(0f, 0f, -1f);
                        Ray ray = new Ray(ori, dest);
                        optiuniMeniu.collideWith(ray, results);
                        if (results.size() > 0) {
                            CollisionResult closest = results.getClosestCollision();
                            if ("da".equals(closest.getGeometry().getName())) {
                                initInterfataJoc(numarJucatori);
                            }
                            if ("nu".equals(closest.getGeometry().getName())) {
                                System.exit(0);
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    };

    private void initInterfataJoc(final int nrJucatori) {
        // Setari de baza
        fazaJoc = 1;
        turaPlayer1 = true;
        numarJucatori = nrJucatori;
        optiuniMeniu.detachAllChildren();
        for (int i = 0; i < 9; i++) {
            varianteAlese[i] = "";
        }

        // Barele
        Picture pic = new Picture("fundalJoc");
        Texture2D tex = (Texture2D) assetManager.loadTexture("Materials/fundalJoc.png");
        pic.setTexture(assetManager, tex, true);

        // adjust picture
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();
        pic.setWidth(width);
        pic.setHeight(height);
        pic.move(0, 0, 0);

        optiuniMeniu.attachChild(pic);

        // Titlu
        createTextButton("titlu", settings.getWidth() / 2 - 30, settings.getHeight() - 40, 200, 40);

        // Clickabile
        genereazaCutiileClickabile();
    }

    public void genereazaCutiileClickabile() {
        int posXInitial = 125;
        int posYInitial = 485;
        int j = 0;
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0) {
                posXInitial = 125;
                posYInitial = posYInitial - 135;
                j = 0;
            }
            Box b = new Box(60, 60, 0); // create cube shape
            Geometry geom = new Geometry("" + i, b);  // create cube geometry from the shape
            Material mat = new Material(assetManager,
                    "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
            mat.setColor("Color", ColorRGBA.Cyan);   // set color of material to blue
            geom.setMaterial(mat);                   // set the cube's material
            geom.setLocalTranslation(new Vector3f(posXInitial + (j * 165), posYInitial, 0));
            cutiiClickabile.attachChild(geom);
            j++;
        }
    }

    public void drawXO(String xOrO, float posX, float posY) {
        Picture pic = new Picture(xOrO);
        Texture2D tex = (Texture2D) assetManager.loadTexture("Materials/" + xOrO + ".png");
        pic.setTexture(assetManager, tex, true);

        // adjust picture
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();
        pic.setWidth(width);
        pic.setHeight(height);
        pic.move(posX, posY, 0);

        optiuniMeniu.attachChild(pic);
    }

    public String checkWin() {
        // Verificari imputite
        if (varianteAlese[0].equals(varianteAlese[1]) && varianteAlese[1].equals(varianteAlese[2])) {
            return varianteAlese[0];
        }
        if (varianteAlese[3].equals(varianteAlese[4]) && varianteAlese[4].equals(varianteAlese[5])) {
            return varianteAlese[3];
        }
        if (varianteAlese[6].equals(varianteAlese[7]) && varianteAlese[7].equals(varianteAlese[8])) {
            return varianteAlese[6];
        }
        if (varianteAlese[0].equals(varianteAlese[3]) && varianteAlese[3].equals(varianteAlese[6])) {
            return varianteAlese[0];
        }
        if (varianteAlese[1].equals(varianteAlese[4]) && varianteAlese[4].equals(varianteAlese[7])) {
            return varianteAlese[1];
        }
        if (varianteAlese[2].equals(varianteAlese[5]) && varianteAlese[5].equals(varianteAlese[8])) {
            return varianteAlese[2];
        }
        if (varianteAlese[0].equals(varianteAlese[4]) && varianteAlese[4].equals(varianteAlese[8])) {
            return varianteAlese[0];
        }
        if (varianteAlese[2].equals(varianteAlese[4]) && varianteAlese[4].equals(varianteAlese[6])) {
            return varianteAlese[2];
        }
        return "";
    }

    public int miscareAI() {
        System.out.println("O incercare aici!");
        List<Integer> posibileMiscari = new ArrayList<Integer>();
        int max = 0;

        for (int i = 0; i < 9; i++) {
            if ("".equals(varianteAlese[i])) {
                varianteAlese[i] = "o";
                if ("o".equals(checkWin())) {
                    return i;
                } else if ("x".equals(checkWin())) {
                    return i;
                } else {
                    posibileMiscari.add(i);
                }
            }
        }

        max = (int) (Math.random() * (posibileMiscari.size() - 1));
        return max;
    }

    public int corespondentaXO(final int nr) {
        if ("x".equals(varianteAlese[nr])) {
            return -2;
        } else if ("o".equals(varianteAlese[nr])) {
            return 1;
        } else {
            return 0;
        }
    }

    public Spatial getGeometryByName(final String name) {
        for (Spatial spat : cutiiClickabile.getChildren()) {
            if (name.equals(spat.getName())) {
                return spat;
            }
        }
        return null;
    }

    public void sfarsitJoc(final String castigator) {
        fazaJoc = 2;
        guiNode.detachAllChildren();
        optiuniMeniu.detachAllChildren();

        // Ecran de sfarsit
        switch (castigator) {
            case "x":
                createTextButton("winner1", settings.getWidth() / 2 - 30, settings.getHeight() - 100, 150, 40);
                break;
            case "o":
                createTextButton("winner2", settings.getWidth() / 2 - 30, settings.getHeight() - 100, 150, 40);
                break;
            case "draw":
                createTextButton("draw", settings.getWidth() / 2 - 30, settings.getHeight() - 100, 150, 40);
                break;
            default:
                System.out.println("Am avut un caz ciudat cu castigator = " + castigator);
                break;
        }

        createTextButton("replay", settings.getWidth() / 2 - 30, settings.getHeight() - 180, 200, 40);
        createTextButton("da", settings.getWidth() / 2 - 50, settings.getHeight() - 300, 30, 30);
        createTextButton("nu", settings.getWidth() / 2 + 50, settings.getHeight() - 300, 30, 30);
        guiNode.attachChild(optiuniMeniu);
    }
}
