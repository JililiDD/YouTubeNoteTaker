# YouNote
   <p>YouTube is always the go-to place for maximum of the population to have the quick lookups at the topics which are of their interest. The users tend to use YouTube to access the information related to cooking recipes, information of technology etc. While watching or learning through these videos, users might require marking out some time points of their interest or write down some notes. Currently, there is no application which provides these both functionalities and so the users must switch between YouTube and the notepad applications for writing and saving notes. Hence, we have developed the YouNote Application which will tackle this problem.</p>
    <p>YouNote has been developed with the objective of assisting the user in their learning process using the YouTube videos. This application allows the user to take the notes while watching the YouTube videos which run through this application itself. Besides educational purpose, this application also has the potential to be used for other purposes. For example, a user who is interested to watch any entertainment kind of video can also mark out the time points can later playback the video between these points. So, YouNote targets a broad range of users from different field and age groups who use YouTube for information, learning or entertainment. In addition, YouNote not only just allows the user to take notes but also allows them to manage and edit the notes. The user is also provided with the flexibility to pause, rewind or replay the video for taking the notes and allows them to go over to something that they didn’t understand.</p>
     <p>YouNote doesn’t have any constraints related to the environments in which it can be used. User can use this application almost everywhere that is in library or classrooms for learning and in public transits for entertainment. YouNote can be executed and supports all android devices such as phones, tablets and Chromebooks. So, the user has the flexibility to use this application on any preferred devices based on their purpose.</p>

## Libraries
**Picasso-2.4.0:** an Android library for image downloading and caching. It is used to load YouTube video image thumbnail for our YouTube search function. Library source: https://github.com/square/picasso

## Code Examples
**Problem 1: How could an activity take data from a fragment inside of it**
<p>We needed to implement a “play back” function to allow user to play the video back to the eslaped time when the note was taken. To do this, we needed the note fragment to send data (eslaped time in this case) to notebook activity which uses the data (elapsed time) to play the video back to a specific time. </p>

```
public void onFragmentInteraction(Uri uri) {
   String uriStr = uri.toString();
   String[] uriStrList = uriStr.split(" ");
   if(uri != null && uriStrList[0].equals("pause")){
       // Pause the video if TakeNote button is pressed in NoteModeFragment.
       // Referred from: http://blog.csdn.net/fengge34/article/details/46391453
       initializeYoutubePlayer(); //b
       player.pause();

       // Take the elapsedTime
       elapsedTime = player.getCurrentTimeMillis();
   }
   else if(uri != null && uriStrList[0].equals("replay")){
       // Convert the passed time (in uriStrList[1]) from string to int
       String noteTime = uriStrList[1];
       int msNoteTime = Integer.valueOf(noteTime);

       //play the video back to the note time
       player.seekToMillis(msNoteTime);
   }
}
```

**Problem 2: We needed to override the method when user press the back button on the phone to navigate user to different UIs based on user’s status**
<p>We override onBackPressed() function for the notebook activity to give an alert of closing current notebook to user and also navigate user to different UIs after the user closes the notebook. For instance, a registered user is navigated to an after-login UI, while a guest user is navigated to the main page once the notebook is closed. </p>

```

```
<p>@Override</br>
public void onBackPressed() {</br>
   Dialog deleteConfirmBox = new android.support.v7.app.AlertDialog.Builder(GuestActivity.this) </br>
           .setMessage("Leave current notebook?")</br>
           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {</br>
               public void onClick(DialogInterface dialog, int whichButton) {</br>
                   if(isRegisteredUser() && previousActivity != null) {</br>
                       Intent intent = new Intent(getApplicationContext(), UserNoteBooks.class);</br>
                       intent.putExtra("USER_TYPE", "REGISTERED");</br>
                       startActivity(intent);</br>
                   } else if(isRegisteredUser() && previousActivity == null){</br>
                       Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);</br>
                       intent.putExtra("USER_TYPE", "REGISTERED");</br>
                       startActivity(intent);</br>
                   } else if (!isRegisteredUser()){</br>
                       Intent intent = new Intent(getApplicationContext(), MainActivity.class);</br>
                       startActivity(intent);</br>
                   }</br>
                   dialog.dismiss();</br>
               }</br>
           })</br>
           .setNegativeButton("No", null)</br>
           .create();</br>
   deleteConfirmBox.show();</br>
}</p>

## Feature Section
1.	Search YouTube videos: </br>
  	 	Allows user to search YouTube videos in our app
2.	Play YouTube videos: </br>
      Allows user to play YouTube videos in our app
3.	Take notes: </br>
      User can take notes while watching a YouTube video
4.	Review, edit and delete notes: </br>
      User can review, edit and delete the notes he/she created earlier
5.	Playback the video:</br>
      User can play the video back to a recorded time point when the corresponding note was created. 
6.	Email notes: </br>
      User can email the notes he/she wrote for a YouTube video to a specified email address
7.	Save and store notebooks Firebase:</br>
      Registered user can save notebooks in Firebase database
8.	Open notebooks: </br>
      Registered user can review his/her notebooks saved in Firebase database at any time
9. To delete note: </br>
      Long press on the note item that needs to be deleted from the displayed note list


## Final Project Status
The minimum, expected and bonus functionalities of the project are completed. However, there are few minor changes to implement before publishing the product.

#### Minimum Functionality
This application is designed for unregistered and registered users. 
Basic functionalities which are available to unregistered user are as follows:
1.	Search YouTube videos (Completed)
2.	Play YouTube videos (Completed)
3.	Take notes for a single YouTube video(Completed)
4.	Review, edit and delete notes(Completed)
5.	Email notes of a single YouTube video to the user’s email address (Completed)
6.	Playback to the time point at which the selected piece of note was taken (Completed)

#### Expected Functionality
Registered user has full access to following functionalities in addition to the unregistered user’s functionalities:
1.	Create notebooks for multiple videos (Completed)
2.	Save and store notebooks at local storage system (Completed by using Firebase)

#### Bonus Functionality:
In addition to the basic and expected functionalities, the bonus functionalities are listed as follows:
1.	Use Firebase for backend implementation (Completed)
2. Implemented the application for Landscape mode (Completed)

## Sources
[1]. “Android onFragmentInteraction(Uri uri) 方法.” [Online]. Available: http://blog.csdn.net/fengge34/article/details/46391453. [Accessed: 06-Apr-2018].

[2]. YouTube. [Online]. Available: https://www.youtube.com/watch?v=x9VkNlB0Z6Y. [Accessed: 06-Apr-2018].

[3]. “android: how to remove the back/home button in the action bar,” Stack Overflow. [Online]. Available: https://stackoverflow.com/a/22313897. [Accessed: 06-Apr-2018].

[4]. “Android: Preventing going back to the previous activity,” Stack Overflow. [Online]. Available: https://stackoverflow.com/a/26492794. [Accessed: 06-Apr-2018].

[5]. “Create a YouTube Client on Android,” Code Envato Tuts . [Online]. Available: https://code.tutsplus.com/tutorials/create-a-youtube-client-on-android--cms-22858. [Accessed: 06-Apr-2018].

[6]. “Android Beginner Tutorial #16 - Play YouTube Videos using Android Player API in Android Studio,” YouTube, 24-Mar-2017. [Online]. Available: https://www.youtube.com/watch?v=W4hTJybfU7s. [Accessed: 06-Apr-2018].

[7]. “android.content.res.Configuration,” Android Developers, 03-Apr-2018. [Online]. Available: https://developer.android.com/reference/android/content/res/Configuration.html. [Accessed: 06-Apr-2018].

[8]. “Send data from activity to fragment in android,” Stack Overflow. [Online]. Available: https://stackoverflow.com/a/22065903. [Accessed: 06-Apr-2018].

[9]. “Menus,” Android Developers, 09-Mar-2018. [Online]. Available: https://developer.android.com/guide/topics/ui/menus.html. [Accessed: 06-Apr-2018].

[10]. “Sliding tabs using ViewPager in Android Studio,” YouTube, 10-Aug-2017. [Online]. Available: https://www.youtube.com/watch?v=zcnT-3F-9JA. [Accessed: 06-Apr-2018].

[11]. “How to pop up a dialog to confirm delete when user long press on the list item?,” android - How to pop up a dialog to confirm delete when user long press on the list item? - Stack Overflow. [Online]. Available: https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item. [Accessed: 06-Apr-2018].

[12]. “Communicating with Other Fragments,” Android Developers, 09-Mar-2018. [Online]. Available: http://developer.android.com/training/basics/fragments/communicating.html. [Accessed: 06-Apr-2018].

[13]. “Android, How to create option Menu,” Stack Overflow. [Online]. Available: https://stackoverflow.com/questions/6439085/android-how-to-create-option-menu. [Accessed: 08-Apr-2018].

[14]. “udacity/and-nd-firebase,” GitHub, 03-Aug-2016. [Online]. Available: https://github.com/udacity/and-nd-firebase. [Accessed: 08-Apr-2018].
