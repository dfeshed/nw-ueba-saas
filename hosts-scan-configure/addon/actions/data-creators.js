import * as ACTION_TYPES from './types';
import { Schedule } from './api';
import Ember from 'ember';
const { Logger } = Ember;
/**
 * Action creator for getting all the schedule
 * @method getAllSchedules
 * @public
 */
const getScheduleConfig = () => ({
  type: ACTION_TYPES.FETCH_SCHEDULE_CONFIG,
  promise: Schedule.getAllSchedules(),
  meta: {
    onSuccess: (response) => {
      Logger.debug(ACTION_TYPES.FETCH_SCHEDULE_CONFIG, response);
    },
    onFailure: (response) => Logger.error(ACTION_TYPES.FETCH_SCHEDULE_CONFIG, response)
  }
});


const saveScheduleConfig = (config, callBackOptions) => ({
  type: ACTION_TYPES.FETCH_SCHEDULE_CONFIG,
  promise: Schedule.updateSchedule(config),
  meta: {
    onSuccess: (response) => {
      callBackOptions.onSuccess(response);
    },
    onFailure: (response) => {
      Logger.error(ACTION_TYPES.FETCH_SCHEDULE_CONFIG, response);
      callBackOptions.onFailure(response);
    }
  }
});

const updateScheduleProperty = (key, value) => {
  let payload = {};
  if (key === 'enabled') {
    payload = {
      scheduleConfig: {
        enabled: value
      }
    };
  } else if (key === 'cpuMax' || key === 'cpuMaxVm') {
    payload = {
      scheduleConfig: {
        scanOptions: {
          [key]: value
        }
      }
    };
  } else if (key === 'recurrenceIntervalUnit') {
    payload = {
      scheduleConfig: {
        scheduleOptions: {
          recurrenceIntervalUnit: value,
          recurrenceInterval: 1
        }
      }
    };
  } else {
    payload = {
      scheduleConfig: {
        scheduleOptions: {
          [key]: value
        }
      }
    };
  }
  return { type: ACTION_TYPES.UPDATE_CONFIG_PROPERTY, payload };
};

export {
  getScheduleConfig,
  updateScheduleProperty,
  saveScheduleConfig
};
