import faker from 'faker';
import _ from 'lodash';

const OS_TYPE = [ 'windows', 'mac', 'linux' ];

const getMachineInfo = (size) => {
  const items = [];
  for (let i = 0; i <= size; i++) {
    const hostName = faker.internet.domainName();
    const hostOsType = faker.random.arrayElement(OS_TYPE);
    const riskScore = faker.random.number(100);
    items.push({
      hostName,
      hostOsType,
      score: riskScore
    });
  }
  return items;
};

const getAggregation = (aggregate, items) => {
  const data = _.chain(items)
    .groupBy((x) => x.hostOsType)
    .map((value, key) => ({ name: key, count: value.length }))
    .value();
  return {
    data
  };
};

export const getHostData = (groupBy, query, size) => {
  const items = getMachineInfo(size);
  const aggregate = getAggregation(groupBy, items);
  return {
    items,
    aggregate
  };
};


