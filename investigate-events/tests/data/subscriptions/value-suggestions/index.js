import faker from 'faker';

function getResponseArray(count, prefix) {
  const returnArray = [];
  while (count !== 0) {
    returnArray.push({
      value: `${prefix}${faker.random.word()}`,
      count: faker.random.number()
    });
    count--;
  }
  return returnArray;
}

export default {
  subscriptionDestination: '/user/queue/investigate/meta/values/suggestions',
  requestDestination: '/ws/investigate/meta/values/suggestions',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const [ prefix ] = (bodyParsed.filter || []).filter((ele) => ele.field === 'prefix');
    const { value } = prefix;

    if (value === 'test') {
      return {
        code: 0,
        data: ['foo', 'bar', 'baz', 'foobar']
      };
    }

    return {
      code: 0,
      data: getResponseArray(10, value)
    };
  }
};