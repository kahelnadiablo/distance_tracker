Distance tracker for Android
================

Get the total distance covered using the GPS.

Distance covered is now updated. Getting total distance travelled.

FLOW:

Check GPS - > Get current location -> Listen to location change - > Update Location -> Compute distance ->Display

NOTE:
You must cast it as new instance if you want to refresh the starting point by Creating a separate Activity 
which will call the Main Activity I have in this project.

Accuracy of the distance covered is not that accurate since the distance will only be computed once the currect
position are received from GPS itself.

Currently not working with trianguar method of pinpointing location and Galaxy Tab 7.0. ( I wonder why? :| )

LICENSE:

No License

a mention on your code comments will do. hahaha!
Happy coding!

