To run this script, just run 'python auto_config.py' (or better yet - 'nohup "python auto_config.py" &').
This script can do three things:
1. Calculate appropriate low-values-score-reducer configurations for the scored Fs: "python auto_config.py --fs".
2. Calculate good alphas and betas: "python auto_config.py --weights".
3. Show and commit the results produced by the previous steps: "python auto_config.py --results".

Usually, one should first run step 1 to "fix" the Fs scores.
Then, run step 2 (which will use the resulting Fs reducers in the process of calculating the weights).
Then, run step 3 to commit the results into the production configuration files.
If you want to edit the results before committing them, you can just edit the results file (the path is stated below).

Configuration:
In common\config directory there are several configuration files.
Each one overrides the previous one. The order is determined by a field named "order".
The highest value overrides the rest. "None" indicates that the file won't be used at all.
In order to override some configuration, the best approach would be to create a new file with
bigger "order" field (instead of editing one of the existing files).
The following can be overridden:
- mongo_ip: the IP of the server hosting the mongo db. Typically should be left unchanged ("localhost").
- aggregated_feature_event_prevalance_stats_path: the path to the production properties file.
  Typically should be left unchanged.
- entity_events_path: the path to the production properties file. Typically should be left unchanged.
- interim_results_path: the path to where the interim results are saved.
  This is a simple JSON file (which means one can edit it if needed).
- START_TIME, END_TIME: the time interval on which to look at when querying data from mongo.
  "None" indicates the first/last document in the collection.
- NUM_OF_ALERTS_PER_DAY: the desirable number of alerts that should be created per day.
- FIXED_W_DAILY, FIXED_W_HOURLY: if for some reason one knows what weight he wants for some F/P
  (and he doesn't want to let the script automatically decide on the value), he can specify it here.
- BASE_ALPHA, BASE_BETA: the default values used for Fs/Ps which aren't noisy at all (or don't have data).
- verbose: indicates whether the script should print debug info.
- show_graphs: indicates whether the script should show graphs. The important information in
  the graphs are already available using the "verbose" property (but not visually).