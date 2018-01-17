# [7.7.0](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/v7.7.0)
## Features
* [ASOC-21094](https://bedfordjira.na.rsa.net/browse/ASOC-21094):&emsp;Edit Breadcrumb
* [ASOC-36088](https://bedfordjira.na.rsa.net/browse/ASOC-36088):&emsp;Pivot to Investigate Events
* [ASOC-44911](https://bedfordjira.na.rsa.net/browse/ASOC-44911):&emsp;Validate Individual Query Pill
* [ASOC-45231](https://bedfordjira.na.rsa.net/browse/ASOC-45231):&emsp;Display permission warning when access restricted
* [ASOC-45232](https://bedfordjira.na.rsa.net/browse/ASOC-45232):&emsp;Combine Investigate landing page and Investigate Event Analysis page
* [ASOC-45815](https://bedfordjira.na.rsa.net/browse/ASOC-45815):&emsp;Configure Email Notifications for Incident Created/Updated Events
* [ASOC-46851](https://bedfordjira.na.rsa.net/browse/ASOC-46851):&emsp;Rename Certificate validation to Server Validation
* [ASOC-47583](https://bedfordjira.na.rsa.net/browse/ASOC-47583):&emsp;Remove Host Count / Hash Lookup in Process Props
* [ASOC-47597](https://bedfordjira.na.rsa.net/browse/ASOC-47597):&emsp;Support for relative time based filters on Host and Files page
* [ASOC-47609](https://bedfordjira.na.rsa.net/browse/ASOC-47609):&emsp;Fresh Install - Virtual/OVA (HouseStark / Drogon)
* [ASOC-47723](https://bedfordjira.na.rsa.net/browse/ASOC-47723):&emsp;Configuration and health metrics for accepted mongo disk usage percentage
* [ASOC-47736](https://bedfordjira.na.rsa.net/browse/ASOC-47736):&emsp;Schedule Scan Start Time
* [ASOC-47854](https://bedfordjira.na.rsa.net/browse/ASOC-47854):&emsp;Endpoint Theme Refinement
* [ASOC-47969](https://bedfordjira.na.rsa.net/browse/ASOC-47969):&emsp;Provide more relative time options for LAST SCAN TIME
* [ASOC-48010](https://bedfordjira.na.rsa.net/browse/ASOC-48010):&emsp;Grouping of Column Groups
* [ASOC-48063](https://bedfordjira.na.rsa.net/browse/ASOC-48063):&emsp;Pivot to Investigate Events from Files
* [ASOC-48070](https://bedfordjira.na.rsa.net/browse/ASOC-48070):&emsp;UI improvements


# [7.6.0](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/v7.6.0)
## Features
* [ASOC-46101](https://bedfordjira.na.rsa.net/browse/ASOC-46101):&emsp;Create context-sensitive HELP for Incident Rules List and Detail pages
* [ASOC-47426](https://bedfordjira.na.rsa.net/browse/ASOC-47426):&emsp;Respond Context-Sensive Help is opening to the default page
* [ASOC-46379](https://bedfordjira.na.rsa.net/browse/ASOC-46379):&emsp;Regression test Audit_Logs/Logstash


# [7.5.0](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/v7.5.0)
## Features
* More preferences support for investigate events
  * [ASOC-42311](https://bedfordjira.na.rsa.net/browse/ASOC-42311):&emsp;Persist (but not edit) the number of packets per page in packet analysis to preferences
  * [ASOC-42521](https://bedfordjira.na.rsa.net/browse/ASOC-42521):&emsp;Persisting Default column group
  * [ASOC-45157](https://bedfordjira.na.rsa.net/browse/ASOC-45157):&emsp;Default Investigation Landing Page
  * [ASOC-45852](https://bedfordjira.na.rsa.net/browse/ASOC-45852):&emsp;Panel sizing should be persisted per user on Expanding Events panel
* OOTB Column group support for event analysis
  * [ASOC-42520](https://bedfordjira.na.rsa.net/browse/ASOC-42520):&emsp;View the default column group and apply other column groups
  * [ASOC-42523](https://bedfordjira.na.rsa.net/browse/ASOC-42523):&emsp;Modify the columns displayed in the events page table - Endpoint
* [ASOC-44912](https://bedfordjira.na.rsa.net/browse/ASOC-44912):&emsp;Suppress Non-applicable & Indicate Expensive Operations
* [ASOC-46078](https://bedfordjira.na.rsa.net/browse/ASOC-46078):&emsp;Classic Sub-Navigation for Incident Rules should point to Ember
* Enhanced Light-theme
  * [ASOC-46405](https://bedfordjira.na.rsa.net/browse/ASOC-46405):&emsp;Login Page Should remain static/dark regardless of users theme
  * [ASOC-46406](https://bedfordjira.na.rsa.net/browse/ASOC-46406):&emsp;Light theme refinement for Investigate - Events & Recon Page
  * [ASOC-46407](https://bedfordjira.na.rsa.net/browse/ASOC-46407):&emsp;MS Edge background image regression
* Option to upload Agent packager config for Endpoint packager
  * [ASOC-43896](https://bedfordjira.na.rsa.net/browse/ASOC-43896):&emsp;Include multiple LD/VLC + protocol selector
  * [ASOC-38959](https://bedfordjira.na.rsa.net/browse/ASOC-38959):&emsp;Channel Filtering - User Interface
  * [ASOC-46321](https://bedfordjira.na.rsa.net/browse/ASOC-46321):&emsp;Log configuration file holds UI specific values
* [ASOC-43956](https://bedfordjira.na.rsa.net/browse/ASOC-43956):&emsp;Use Preference to select Wall Clock Time for query
* [ASOC-29057](https://bedfordjira.na.rsa.net/browse/ASOC-29057):&emsp;Set/Persist User preference for default view



# [7.4.0](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/v7.4.0)
## Features
* [ASOC-657](https://bedfordjira.na.rsa.net/browse/ASOC-657):&emsp;Support additional truncate options on application rules in UI rule editor
* [ASOC-45197](https://bedfordjira.na.rsa.net/browse/ASOC-45197):&emsp;Remove majority of recon page when user modifies URL to event restricted by query prefix
* [ASOC-45233](https://bedfordjira.na.rsa.net/browse/ASOC-45233):&emsp;Synchronize Ember and ExtJS Investigate sub-menus
* [ASOC-21100](https://bedfordjira.na.rsa.net/browse/ASOC-21100):&emsp;Add Breadcrumb
* [ASOC-41656](https://bedfordjira.na.rsa.net/browse/ASOC-41656):&emsp;Delete Breadcrumb
* [ASOC-42195](https://bedfordjira.na.rsa.net/browse/ASOC-42195):&emsp;Add Pagination Controls
* [ASOC-42894](https://bedfordjira.na.rsa.net/browse/ASOC-42894):&emsp;Persist (but not edit) number of packets per page in packet analysis
* [ASOC-42324](https://bedfordjira.na.rsa.net/browse/ASOC-42324):&emsp;To override/persist default download format for log and packet events
* [ASOC-42504](https://bedfordjira.na.rsa.net/browse/ASOC-42504):&emsp;Internal Actions - Actions on meta values found in the Event Analysis meta panel -links to classic Navigate
* [ASOC-42508](https://bedfordjira.na.rsa.net/browse/ASOC-42508):&emsp;Actions on specific Event found in the Event Analysis meta panel -Link to classic Events reconstruction
* [ASOC-42511](https://bedfordjira.na.rsa.net/browse/ASOC-42511):&emsp;Internal Actions - in Events page
* [ASOC-42512](https://bedfordjira.na.rsa.net/browse/ASOC-42512):&emsp;Internal Actions - in Event Analysis event header
* [ASOC-42895](https://bedfordjira.na.rsa.net/browse/ASOC-42895):&emsp;External Actions - Enable right click and actions in Event Analysis event header
* [ASOC-45001](https://bedfordjira.na.rsa.net/browse/ASOC-45001):&emsp;Per user to save/persist any settings
* [ASOC-45619](https://bedfordjira.na.rsa.net/browse/ASOC-45619):&emsp;Internal Actions - Actions on meta values found in the Event Analysis meta panel -Investigate host/endpoint
* [ASOC-40019](https://bedfordjira.na.rsa.net/browse/ASOC-40019):&emsp;Provide the ability to search multiple fields in parallel
* [ASOC-42210](https://bedfordjira.na.rsa.net/browse/ASOC-42210):&emsp;Respond - ability to send notifications for the following incident notification types
* [ASOC-42220](https://bedfordjira.na.rsa.net/browse/ASOC-42220):&emsp;Theme - Main navigation color palette remains static
* [ASOC-42222](https://bedfordjira.na.rsa.net/browse/ASOC-42222):&emsp;Theme - Apply light theme color palette for investigate events
* [ASOC-42236](https://bedfordjira.na.rsa.net/browse/ASOC-42236):&emsp;Respond - Port Incident Agg Configuration Page (Non-Rule Builder Components)
* [ASOC-43751](https://bedfordjira.na.rsa.net/browse/ASOC-43751):&emsp;Respond - Public Facing Launch API - P1 Append Journal Entry
* [ASOC-45229](https://bedfordjira.na.rsa.net/browse/ASOC-45229):&emsp;Respond - Ensure Meta Entities do not Break Respond
* [ASOC-45581](https://bedfordjira.na.rsa.net/browse/ASOC-45581):&emsp;Create route for aggregation-rules that appears to be alongside Classic Nav
* [ASOC-45689](https://bedfordjira.na.rsa.net/browse/ASOC-45689):&emsp;Ember page fallback language to English
* [ASOC-45009](https://bedfordjira.na.rsa.net/browse/ASOC-45009):&emsp;Changes to handle multiple ip addresses per adapter
* [ASOC-44994](https://bedfordjira.na.rsa.net/browse/ASOC-44994):&emsp;Remove Full Disk Scan from UI
* [ASOC-42259](https://bedfordjira.na.rsa.net/browse/ASOC-42259):&emsp;Stabilization for UI post NW integration (QE Activity) Part 1
* [ASOC-38645](https://bedfordjira.na.rsa.net/browse/ASOC-38645):&emsp;System Information - Security Products/Windows Patches
