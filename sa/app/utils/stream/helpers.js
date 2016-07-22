/**
 * @file Stream helper utilities
 * Utility that looks up the socket config for a modelName-method pair.
 * Applies the default socketUrl for the modelName if missing from a modelName-method config.
 * @public
 */
import config from 'sa/config/environment';

export default {
  findSocketConfig(modelName, method) {
    let modelConfig = ((config.socketRoutes || {})[modelName] || {});
    let methodConfig = modelConfig[method];

    if (methodConfig) {
      methodConfig.socketUrl = modelConfig.socketUrl;
    }
    return methodConfig;
  }
};
