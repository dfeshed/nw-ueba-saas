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
    const [ metaName ] = (bodyParsed.filter || []).filter((ele) => ele.field === 'metaName');
    const { value } = prefix;

    if (value === 'test') {
      return {
        code: 0,
        data: [{ value: 'testfoo' }, { value: 'testbar' }, { value: 'testfoobar' }]
      };
    } else if (value === 'test100') {
      return {
        code: 0,
        data: Array.from({ length: 100 }, () => ({ value: faker.random.word() }))
      };
    } else if (metaName.value == 'service') {
      return {
        code: 0,
        data: Array.from({ length: 10 }, () => ({ value: 20 }))
      };
    } else if (value == 'test0') {
      return {
        code: 0,
        data: []
      };
    }

    return {
      code: 0,
      data: getResponseArray(10, value)
    };
  }
};