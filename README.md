# Study Buddy
Study Buddy is an Android app that offers a reward for productivity by tying the user’s commitment to a virtual pet which directly benefits from their compliance. When the user remains accountable for their tasks, their virtual pet will grow, improve in mood, and trust the user.

## Important Links
[Requirements](https://docs.google.com/document/d/17MSjLnf0IBkxtAoBxkW1J8jti30xX3Pm1pVyJA1Q3rA/edit?usp=sharing)

[Team Policies](https://docs.google.com/document/d/1g6Af2lr7RfceBt78roCZq0QuGHKQK92-_P4S3bElzJM/edit?usp=sharing)

[Beta Release Presentation Slides](https://docs.google.com/presentation/d/1c-MSeEbuZIKKDbdN7N9VCPRTijrTlrTPUW3-zUKuEPw/edit?usp=sharing)
## Building and Testing the System
Gradle automatically builds the system. To test the system, run the JUnit tests found within the testing folder (app >> src >> test).
## Running the System
The app can be run one of two ways: on an Android device or using an Android virtual machine. To run Study Buddy on an Android virtual machine, a virtual machine must first be downloaded and installed. This can be done through Android Studio, which can be downloaded [here](https://developer.android.com/studio). The instructions to set up a virtual device through Android Studio are as follows:

 1. In Android Studio, navigate to Tools in the toolbar and select AVD Manager
 2. Select Create Virtual Device
 3. Choose the virtual device you would like to install and click next
 4.  Choose and download a System Image, then click next
	 - Any image above API level 24 is fine, as that is the oldest version we are targeting
 5. Name the Virtual Device and click Finish
 6. Near the upper right corner of Android Studio, in the drop down menu next to the green hammer, select 'app'.
 7. In the drop down menu to the right of where you selected 'app', select your Virtual Device
 8. Click the green play button next to where the virtual device was selected
 9. You should now be running Study Buddy on your Virtual Device
 
To run Study Buddy on your Android device also requires Android Studio (for now). 

 1. First you must enable [developer mode](https://developer.android.com/studio/debug/dev-options) on your Android device.
 2. Open up Study Buddy master in Android studio.
 3. Connect your Android device to your computer.
 4. On your device, follow on-screen prompts to add the computer to your list of trusted devices for developing.
 5. Near the upper right corner of Android Studio, in the drop down menu next to the green hammer, select 'app'.
 6. In the drop down menu to the right of where you selected 'app', select your Android Device.
 7. Click the green play button next to where the virtual device was selected.
 8. You should now be running Study Buddy on your Android Device.
