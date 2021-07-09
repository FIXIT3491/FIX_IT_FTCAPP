/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.gamecode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.RC;
import org.firstinspires.ftc.teamcode.Robots.Beyonce;
import org.firstinspires.ftc.teamcode.opmodesupport.AutoOpMode;

import java.util.List;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Ultimate Goal game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous
public class CompFinalAuto extends AutoOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    private Servo mechanicalBlock;

    private DcMotor shooter = null;
    Beyonce beyonce = new Beyonce();

    /**
    * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
    * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
    * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
    * web site at https://developer.vuforia.com/license-manager.
    *
    * Vuforia license keys are always 380 characters long, and look as if they contain mostly
    * random data. As an example, here is a example of a fragment of a valid key:
    *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
    * Once you've obtained a license key, copy the string from the Vuforia web site
    * and paste it in to your code on the next line, between the double quotes.
    */
    private static final String VUFORIA_KEY = RC.VUFORIA_LICENSE_KEY;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    private VoltageSensor ExpansionHub2_VoltageSensor;


    @Override
    public void runOp() throws InterruptedException {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that first.
        initVuforia();
        initTfod();
        int state = 0;

        //Declaring Hardware
        mechanicalBlock = hardwareMap.get(Servo.class, "MechanicalBlock");

        ColorSensor colorSensorL;
        colorSensorL = (ColorSensor) hardwareMap.get("ColourSensorL");

        ColorSensor colorSensorR;
        colorSensorR = (ColorSensor) hardwareMap.get("ColourSensorR");

        ExpansionHub2_VoltageSensor = hardwareMap.get(VoltageSensor.class, "Expansion Hub 2");

        shooter = hardwareMap.get(DcMotor.class, "Shooter");

        //State Value of Red light
        int redVal = 200;

        /*
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         */
        if (tfod != null) {
            tfod.activate();

            /*
             * The TensorFlow software will scale the input images from the camera to a lower resolution.
             * This can result in lower detection accuracy at longer distances (> 55cm or 22").
             * If your target is at distance greater than 50 cm (20") you can adjust the magnification value
             * to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
             * should be set to the value of the images used to create the TensorFlow Object Detection model
             * (typically 1.78 or 16/9).
             *
             * Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
             */
            tfod.setZoom(2.5, 1.78);
        }

        //Wait for the game to begin
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        setMechanicalBlockTurnOff();

        waitForStart();

        //Start of Autonomous

        //Shooting rings
        shooterOn();
        sleep(2000);
        beyonce.Shoot();
        beyonce.Shoot();
        beyonce.Shoot();
        shooterOff();
        sleep(100);


        beyonce.Ramp.setPosition(0.38);

        //Declaring variables
        int objects;

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                int iterate = 0;
                boolean targetFound = false;
                //while (iterate < 10000) {
                if (tfod != null) {
                    /*
                    *  cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
                    *  getUpdatedRecognitions() will return null if no new information is available since
                    *  the last time that call was made.
                    */

                    //Setting up a list for Ring Recognition
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                    //Detect number of rings
                    if (updatedRecognitions != null) {
                        objects = updatedRecognitions.size();
                        telemetry.addData("# Object Detected", objects);

                        //Step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) { //arraylist
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());

                            //Detecting 1 Ring
                            if (recognition.getLabel().equals(LABEL_FIRST_ELEMENT)) {
                                state = 2;
                                telemetry.addData("state", 2);

                            //Detecting 4 Rings
                            } else if (recognition.getLabel().equals(LABEL_SECOND_ELEMENT)) {
                                state = 1;
                                telemetry.addData("state", 1);
                            }
                            telemetry.addData("in", 0);
                            break;
                        }

                        telemetry.addData("out", 0);

                        //0 Ring Randomization
                        if (state == 0) {
                            telemetry.addData("Randomization:", "Zero");

                            //Shooting Rings
                            shooterOn();
                            sleep(2000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            shooterOff();

                            //Close Wobble Grabber, Drive into field wall
                            beyonce.ClawClose();
                            beyonce.DriveBackward(0.7);
                            sleep(800);

                            //Continues driving along field wall until detecting first red line
                            while (opModeIsActive() && redVal > colorSensorL.red()) {
                                beyonce.StrafeRight(1);
                                telemetry.addData("red", colorSensorL.red());
                            }
                            beyonce.Stop();
                            telemetry.addData("Red Line 1:", "Detected");

                            //Drive into field wall
                            beyonce.DriveBackward(0.5);
                            sleep(500);
                            beyonce.Stop();

                            //Drive away from field wall
                            beyonce.DriveForward(0.5);
                            sleep(100);
                            beyonce.Stop();

                            //Continues driving until detecting target zone edge
                            while (opModeIsActive() && redVal > colorSensorR.red()) {
                                beyonce.DriveForward(0.5);
                                //red = opModeIsActive() && 120 < colorSensorL.red();
                                telemetry.addData("red", colorSensorR.red());
                            }
                            telemetry.addData("Target Zone A Edge:", "Detected");
                            beyonce.Stop();
                            sleep(1000);

                            //Drop Wobble Goal
                            beyonce.ArmDown(-0.5);
                            sleep(1800);
                            beyonce.ArmDown(-0.25);
                            sleep(750);
                            beyonce.ClawOpen();
                            sleep(500);
                            beyonce.DriveForward(0.75);
                            sleep(300);

                            //Drive away from Wobble Goal
                            beyonce.DriveForward(0.5);
                            sleep(400);
                            beyonce.Stop();

                            //Deploy BeatInStick
                            beyonce.Beat(-0.1);
                            sleep(100);
                            beyonce.Stop();

                            //Terminate Program
                            targetFound = true;

                        //1 Ring Randomization
                        } else if (state == 1) {
                            telemetry.addData("Randomization:", "Single");

                            //Shooting Rings
                            shooterOn();
                            sleep(2000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            shooterOff();

                            //Close Wobble Grabber, Drive into field wall
                            beyonce.ClawClose();
                            beyonce.DriveBackward(0.7);
                            sleep(800);

                            //Drive along field wall
                            beyonce.StrafeRight(1);
                            sleep(2000);
                            beyonce.Stop();

                            //Drive into field wall
                            beyonce.DriveBackward(0.5);
                            sleep(500);
                            beyonce.Stop();

                            //Continues driving along field wall until detecting first red line
                            while (opModeIsActive() && redVal > colorSensorL.red()) {
                                beyonce.StrafeRight(0.5);
                                telemetry.addData("red", colorSensorL.red());
                            }
                            beyonce.Stop();
                            telemetry.addData("Red Line 1:", "Detected");

                            //Drive into field wall
                            beyonce.DriveBackward(0.7);
                            sleep(400);
                            beyonce.Stop();

                            //Drive along field wall
                            beyonce.StrafeRight(0.7);
                            sleep(300);
                            beyonce.Stop();

                            //Continues driving along field wall until detecting first red line
                            while (opModeIsActive() && redVal > colorSensorL.red()) {
                                beyonce.StrafeRight(0.5);
                                //red = opModeIsActive() && 120 < colorSensorL.red();
                                telemetry.addData("red", colorSensorL.red());
                            }
                            beyonce.Stop();
                            telemetry.addData("Red Line 3:", "Detected");

                            //Drive into field wall
                            beyonce.DriveBackward(0.7);
                            sleep(750);

                            //Continues driving until detecting target zone
                            while (opModeIsActive() && redVal > colorSensorR.red()) {
                                beyonce.DriveForward(0.5);
                                //red = opModeIsActive() && 120 < colorSensorL.red();
                                telemetry.addData("red", colorSensorR.red());
                            }
                            telemetry.addData("Target Zone B:", "Detected");
                            beyonce.Stop();
                            sleep(500);

//                            //Drive forward to allow arm to drop inside of Target Zone
//                            beyonce.DriveForward(0.7);
//                            sleep(200);
//                            beyonce.Stop();

                            //Continues driving until detecting target zone edge
                            while (opModeIsActive() && redVal > colorSensorR.red()) {
                                beyonce.DriveForward(0.5);
                                //red = opModeIsActive() && 120 < colorSensorL.red();
                                telemetry.addData("red", colorSensorR.red());
                            }
                            telemetry.addData("Target Zone B Edge:", "Detected");
                            beyonce.Stop();
                            sleep(1000);

                            //Drop Wobble Goal
                            beyonce.ArmDown(-0.5);
                            sleep(1800);
                            beyonce.ArmDown(-0.25);
                            sleep(750);
                            beyonce.ClawOpen();
                            sleep(500);
                            beyonce.DriveForward(0.75);
                            sleep(300);

                            //Drive to park
                            beyonce.StrafeLeft(0.7);
                            sleep(1000);
                            beyonce.Stop();

                            //Deploy BeatInStick
                            beyonce.Beat(-0.1);
                            sleep(100);
                            beyonce.Stop();

                            //Terminate Program
                            targetFound = true;

                        //4 Rings Randomization
                        } else if (state == 2) {
                            telemetry.addData("Randomization:", "Quad");

                            //Shooting Rings
                            shooterOn();
                            sleep(2000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            shooterOff();

                            //Close Wobble Grabber, Drive into field wall
                            beyonce.ClawClose();
                            beyonce.DriveBackward(0.7);
                            sleep(800);

                            //Drive along field wall
                            beyonce.StrafeRight(1);
                            sleep(4000);
                            beyonce.Stop();

                            //Drive into field wall
                            beyonce.DriveBackward(0.5);
                            sleep(500);
                            beyonce.Stop();

                            //Drive away from field wall
                            beyonce.DriveForward(0.5);
                            sleep(100);
                            beyonce.Stop();

                            //Continues driving until detecting target zone edge
                            while (opModeIsActive() && redVal > colorSensorR.red()) {
                                beyonce.DriveForward(0.5);
                                //red = opModeIsActive() && 120 < colorSensorL.red();
                                telemetry.addData("red", colorSensorR.red());
                            }
                            telemetry.addData("Target Zone C Edge:", "Detected");
                            beyonce.Stop();
                            sleep(1000);

                            //Drop Wobble Goal
                            beyonce.ArmDown(-0.5);
                            sleep(1800);
                            beyonce.ArmDown(-0.25);
                            sleep(750);
                            beyonce.ClawOpen();
                            sleep(500);
                            beyonce.DriveForward(0.75);
                            sleep(300);

                            //Drive to park
                            beyonce.StrafeLeft(1);
                            sleep(1500);
                            beyonce.Stop();

                            //Deploy BeatInStick
                            beyonce.Beat(-0.1);
                            sleep(100);
                            beyonce.Stop();

                            //Terminate Program
                            targetFound = true;

                        //Incase of a weird value returned - Runs as if 0 Rings
                        } else {
                            telemetry.addData("Randomization:", "Unknown, running as if Zero");

                            //Shooting Rings
                            shooterOn();
                            sleep(2000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            beyonce.Shoot();
                            sleep(1000);
                            shooterOff();

                            //Close Wobble Grabber, Drive into field wall
                            beyonce.ClawClose();
                            beyonce.DriveBackward(0.7);
                            sleep(800);

                            //Continues driving along field wall until detecting first red line
                            while (opModeIsActive() && redVal > colorSensorL.red()) {
                                beyonce.StrafeRight(1);
                                telemetry.addData("red", colorSensorL.red());
                            }
                            beyonce.Stop();
                            telemetry.addData("Red Line 1:", "Detected");

                            //Drive into field wall
                            beyonce.DriveBackward(0.5);
                            sleep(500);
                            beyonce.Stop();

                            //Drive away from field wall
                            beyonce.DriveForward(0.5);
                            sleep(100);
                            beyonce.Stop();

                            //Continues driving until detecting target zone edge
                            while (opModeIsActive() && redVal > colorSensorR.red()) {
                                beyonce.DriveForward(0.5);
                                //red = opModeIsActive() && 120 < colorSensorL.red();
                                telemetry.addData("red", colorSensorR.red());
                            }
                            telemetry.addData("Target Zone C Edge:", "Detected");
                            beyonce.Stop();
                            sleep(1000);

                            //Drop Wobble Goal
                            beyonce.ArmDown(-0.5);
                            sleep(1800);
                            beyonce.ArmDown(-0.25);
                            sleep(750);
                            beyonce.ClawOpen();
                            sleep(500);
                            beyonce.DriveForward(0.75);
                            sleep(300);

                            //Drive away from Wobble Goal
                            beyonce.DriveForward(0.5);
                            sleep(400);
                            beyonce.Stop();

                            //Deploy BeatInStick
                            beyonce.Beat(-0.1);
                            sleep(100);
                            beyonce.Stop();

                            //Terminate Program
                            targetFound = true;
                        }

                        //Update Telemetry
                        telemetry.update();
                    }

                    //Terminate Program
                    if (targetFound) {
                        break;
                    }
                }
            }

            //Deactivate Tensorflow
            if (tfod != null) {
                tfod.shutdown();
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia () {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod () {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

    //Functions for Shooters
    private void shooterOn() {
        ((DcMotorEx)shooter).setVelocity(2800);
    }
    private void shooterOff() {
        ((DcMotorEx)shooter).setVelocity(0);
    }

    //Functions for Wobble Grabber Mechanical Block
    private void setMechanicalBlockTurnOn() {
        mechanicalBlock.setPosition(0.5);
    }
    private void setMechanicalBlockTurnOff()
    {
        mechanicalBlock.setPosition(0.2);
    }
}

