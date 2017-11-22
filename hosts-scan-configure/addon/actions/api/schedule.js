import { promiseRequest } from 'streaming-data/services/data-access/requests';

const getAllSchedules = () => {
  return promiseRequest({
    method: 'get',
    modelName: 'schedule',
    query: {
      data: {}
    }
  });
};

const updateSchedule = (data) => {
  return promiseRequest({
    method: 'update',
    modelName: 'schedule',
    query: { data }
  });
};

export {
  getAllSchedules,
  updateSchedule
};
