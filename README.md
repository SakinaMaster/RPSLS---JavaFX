# RPSLS-JavaFX
<b>RPSLS</b> stands for <b>Rock, Paper, Scissors, Lizard, Spock.</b><br/>
This is an augmented version of the traditional Rock, Paper, Scissors game. <br/>
<h3><b>Steps to run the application: </b></h3>
<ul><li>Run the Server program. You may use the default port provided, or change it to a
customized port, then click “Server On”.</li>
<li>Run the Client program. You may use the default port and IP provided, or customize it.
Then click “Connect to Server”. Now you may enter the username of your choice. Note that the program will continue to reject the name (and you will not see a client list) if it is not unique in order to maintain unique identification for each client connected. Then press “SUBMIT NAME” to send name to server.</li>
<li>Now you will see a list of all of the clients online. Choose a client to challenge and enter their name in the TextField at the bottom and press “Challenge”. If the player is engaged in an ongoing game of RPSLS, you will be notified.</li>
<li>The opponent of your choice will either Accept or Decline your challenge and you will be notified accordingly. If some other client wishes to challenge you, you must either Accept or Decline their request.</li>
<li>After a single round, you will know who won and you may choose another client to challenge. (Please note that you may not challenge yourself to a game of RPSLS)</li>
  <li>You can challenge as many other clients as you wish! Enjoy the game!</li></ul>

 <b><h3>Features:</b></h3>
 <ul><li>This is s JavaFX implementation which handles multiple games concurrently.</li>
  <li>The client list is updated in real time and sent to all clients which are connected to the server.</li>
  <li> The players can challenge other players after completing a round or they can quit.</li></ul>
 
