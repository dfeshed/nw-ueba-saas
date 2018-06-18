import { lookup } from 'ember-dependency-lookup';

const getAllSchedules = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'get',
    modelName: 'schedule',
    query: {
      data: {}
    }
  });
};

const updateSchedule = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'update',
    modelName: 'schedule',
    query: { data }
  });
};

export {
  getAllSchedules,
  updateSchedule
};
