/**
 * Billboarding.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.fxyz3d.samples.utilities;

import java.util.Arrays;
import java.util.Random;

import org.fxyz3d.controls.CameraViewControl;
import org.fxyz3d.controls.ComboBoxControl;
import org.fxyz3d.controls.ControlPanel;
import org.fxyz3d.controls.SectionLabel;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.samples.shapes.ShapeBaseSample;
import org.fxyz3d.scene.BillboardNode;
import org.fxyz3d.scene.BillboardNode.BillboardMode;
import org.fxyz3d.scene.CameraView;
import org.fxyz3d.shapes.primitives.TorusMesh;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author Dub
 */
public class Billboarding extends ShapeBaseSample<BillBoard> {
    public static class Launcher {

        public static void main(String[] argv) {
            Billboarding.main(argv);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    protected CameraView                        cameraView;
    private final BooleanProperty               active = new SimpleBooleanProperty(this, "Billboarding Active");           // Flag
                                                                                                                           // for
                                                                                                                           // toggling
                                                                                                                           // behavior
    private final ObjectProperty<BillboardMode> mode   = new SimpleObjectProperty<BillboardMode>(this, "BillBoard Mode",
                                                                                                 BillboardMode.SPHERICAL) {

                                                           @Override
                                                           protected void invalidated() {
                                                               if (model != null) {
                                                                   model.setBillboardMode(getValue());
                                                                   System.out.println("mode changed");
                                                               }
                                                           }

                                                       };
    // **************************************************************************
    private final BooleanProperty useCameraView = new SimpleBooleanProperty(this, "CameraView Enabled", true);

    @Override
    protected void addMeshAndListeners() {
        model.activeProperty().bind(active);

        model.parentProperty().addListener(l -> {
            if (model.getParent() != null) {
                final CameraViewControl camView = new CameraViewControl(useCameraView, subScene, mainPane);
                camView.visibleProperty().bind(useCameraView);

                StackPane.setAlignment(camView, Pos.BOTTOM_RIGHT);
            }
        });

    }

    @Override
    protected Node buildControlPanel() {
        ControlPanel panel = ControlFactory.buildSingleListControlPanel();
        panel.addToRoot(new SectionLabel("BillBoarding Properties"), ControlFactory.buildCheckBoxControl(active),
                        new ComboBoxControl<>("Billboarding Mode", mode, Arrays.asList(BillboardMode.values()), false),
                        new SectionLabel("CameraView"), ControlFactory.buildCheckBoxControl(useCameraView),
                        ControlFactory.buildCheckBoxControl(useSkybox));

        return panel;
    }

    @Override
    protected void createMesh() {
        model = new BillBoard(camera);
        model.setBillboardMode(mode.get());

        Platform.runLater(() -> {
            // createCameraView();
            appendSubScene();
        });

    }

    private void appendSubScene() {
        camera.setTranslateZ(-2000);
        initFirstPersonControls(subScene);

        // Make a bunch of semi random Tori and stuff : from torustest
        final Group torusGroup = new Group();
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            // A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (r.nextFloat() * 300) + 50;
            float randomTubeRadius = (r.nextFloat() * 100) + 1;
            int randomTubeDivisions = (int) ((r.nextFloat() * 64) + 1);
            int randomRadiusDivisions = (int) ((r.nextFloat() * 64) + 1);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            boolean ambientRandom = r.nextBoolean();
            boolean fillRandom = r.nextBoolean();

            TorusMesh torus = new TorusMesh(randomTubeDivisions, randomRadiusDivisions, randomRadius, randomTubeRadius);
            torus.setMaterial(new PhongMaterial(randomColor));

            double translationX = Math.random() * 1024 * 1.95;
            if (Math.random() >= 0.5) {
                translationX *= -1;
            }
            double translationY = Math.random() * 1024 * 1.95;
            if (Math.random() >= 0.5) {
                translationY *= -1;
            }
            double translationZ = Math.random() * 1024 * 1.95;
            if (Math.random() >= 0.5) {
                translationZ *= -1;
            }
            Translate translate = new Translate(translationX, translationY, translationZ);
            Rotate rotX = new Rotate(Math.random() * 360, Rotate.X_AXIS);
            Rotate rotY = new Rotate(Math.random() * 360, Rotate.Y_AXIS);
            Rotate rotZ = new Rotate(Math.random() * 360, Rotate.Z_AXIS);

            torus.getTransforms().addAll(translate, rotX, rotY, rotZ);
            // torus.getTransforms().add(translate);
            torusGroup.getChildren().add(torus);
        }
        group.getChildren().add(torusGroup);

    }

    /*
     * private void createCameraView() { cameraView = new CameraView(subScene);
     * cameraView.setFitWidth(250); cameraView.setFitHeight(200);
     * cameraView.setFirstPersonNavigationEabled(true);
     * cameraView.setFocusTraversable(true);
     * cameraView.getCamera().setTranslateZ(-2500);
     * cameraView.getCamera().setTranslateX(500);
     * 
     * StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT);
     * StackPane.setMargin(cameraView, new Insets(20));
     * 
     * mainPane.getChildren().add(cameraView);
     * 
     * cameraView.startViewing(); }
     */
    private void initFirstPersonControls(SubScene scene) {
        // make sure Subscene handles KeyEvents
        scene.setOnMouseEntered(e -> {
            scene.requestFocus();
        });
        // First person shooter keyboard movement
        scene.setOnKeyPressed(event -> {
            double change = 10.0;
            // Add shift modifier to simulate "Running Speed"
            if (event.isShiftDown()) {
                change = 50.0;
            }
            // What key did the user press?
            KeyCode keycode = event.getCode();
            // Step 2c: Add Zoom controls
            if (keycode == KeyCode.W) {
                camera.setTranslateZ(camera.getTranslateZ() + change);
            }
            if (keycode == KeyCode.S) {
                camera.setTranslateZ(camera.getTranslateZ() - change);
            }
            // Step 2d: Add Strafe controls
            if (keycode == KeyCode.A) {
                camera.setTranslateX(camera.getTranslateX() - change);
            }
            if (keycode == KeyCode.D) {
                camera.setTranslateX(camera.getTranslateX() + change);
            }

        });

    }

}

//******************            BillBoard            ***********************
class BillBoard extends BillboardNode<ImageView> {

    private final Node      other;
    private final ImageView view;

    public BillBoard(Node other) {
        super();
        this.other = other;
        view = new ImageView(new Image("http://overshoot.tv/sites/overshoot.tv/files/Oak_Tree_0.png"));
        view.setFitWidth(200);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        setDepthTest(DepthTest.ENABLE);
        getChildren().add(view);
    }

    @Override
    protected ImageView getBillboardNode() {
        return view;
    }

    @Override
    protected Node getTarget() {
        return other;
    }
}
