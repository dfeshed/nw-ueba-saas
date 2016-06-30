Usage:
------
To run this script, just run 'python auto_config.py' (or better yet - 'nohup "python auto_config.py" &').
This script can do three things:
1. Calculate appropriate low-values-score-reducer configurations for the scored Fs: "python auto_config.py fs".
2. Calculate good alphas and betas: "python auto_config.py weights".
3. Show and commit the results produced by the previous steps: "python auto_config.py results".
Each one gets different arguments - just run with "-h" to see help about the arguments.

Usually, one should first run step 1 to "fix" the Fs scores.
Then, run step 2 (which will use the resulting Fs reducers in the process of calculating the weights).
Then, run step 3 to commit the results into the production configuration files.
If you want to edit the results before committing them, you can just edit the results file (the path is stated below).
Note that if you first run step 3 and only then run step 2 - the calculated reducers won't be taken into account while
calculating alphas and betas (which is bad).

Configuration:
--------------
In common\config directory there are several configuration files.
Each one overrides the previous one. The order is determined by a field named "order".
The highest value overrides the rest. "None" indicates that the file won't be used at all.
In order to override some configuration, the best approach would be to create a new file with
bigger "order" field (instead of editing one of the existing files).
The following can be overridden:
- mongo_ip: the IP of the server hosting the mongo db. Typically should be left unchanged ("localhost").
- aggregated_feature_event_prevalance_stats_path: the path to the production properties file.
  It's important to notice that if the installed version of the fortscale product is prior 2.6,
  config_poc_26.py should be changed so it'll be ignored ("order = ..." should be changed to "order = None").
- aggregated_feature_event_prevalance_stats_additional_path: the location of the additional aggregation models.
  This is needed only in version 2.6.
- entity_events_path: the path to the production properties file. Typically should be left unchanged.
- interim_results_path: the path to where the interim results are saved.
  This is a simple JSON file (which means one can edit it if needed).
- START_TIME, END_TIME: the time interval on which to look at when querying data from mongo.
  "None" indicates the first/last document in the collection.
- NUM_OF_ALERTS_PER_DAY: the desirable number of alerts that should be created per day.
- FIXED_W_DAILY, FIXED_W_HOURLY: if for some reason one knows what weight he wants for some F/P
  (and he doesn't want to let the script automatically decide on the value), he can specify it here.
- BASE_ALPHA, BASE_BETA: the default values used for Fs/Ps which aren't noisy at all (or don't have data).
- F_REDUCER_TO_MIN_POSITIVE_SCORE: a map from name of F to the minimal value which is allowed to get a positive score.
  This is used in the process of finding the best F reducers: the algorithm won't consider reducers which don't obey
  this limit while searching for the best reducers.
- verbose: indicates whether the script should print debug info.
- show_graphs: indicates whether the script should show graphs. The important information in
  the graphs are already available using the "verbose" property (but not visually).
- dry: set to True if you don't want to update the interim results file according to the algorithm's result.

Prerequisites:
--------------
The weights algorithm assumes that there's an accessible mongo db with the entity events collections
populated (all those starting with "entity_event_").
The collections are used for two purposes:
- Get the distribution of Fs and Ps. The distribution is used in the first part of the
  algorithm - in order to give a penalty for each F and P for its noisiness.
- Get all of the entity events. This is used in the second part of the algorithm - in order to iteratively
  decrease alphas and betas until the top entity events have a good ratio of participating Fs and Ps.

This fs reducers algorithm assumes that there's an accessible mongo db with the collections starting with
"scored___aggr_event" populated. These are used in order to find which Fs are noisy (such that low values
reduction can help reducing the noise).

Because both algorithms need the original F scores (before the reduction made in production), they access the
production properties file aggregated-feature_event-prevalance-stats.properties in order to see what were the
production reducers (in order to undo their reduction).