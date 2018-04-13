const randInt = function(min = 0, max = 20) {
  return parseInt(min + Math.random() * (max - min), 10);
};

const fiveMinutes = 5 * 60 * 1000;

// Generates random summary data for a single entity.
// These match the JSON structure currently expected by 10.6 for backwards compatability.
// Each call returns a random number of records.
const entityRecords = function() {
  const lastUpdated = String(Number(new Date() - fiveMinutes));
  const all = [
    { name: 'Archer', count: null, url: 'www.google.com', criticality: 'Low', riskRating: 'Medium', lastUpdated },
    { name: 'Incidents', count: String(randInt()), lastUpdated },
    { name: 'Alerts', count: String(randInt()), lastUpdated },
    { name: 'LIST', count: String(randInt()), lastUpdated },
    { name: 'Users', count: String(randInt()), lastUpdated },
    { name: 'IOC', count: String(randInt()), lastUpdated },
    { name: 'Modules', count: String(randInt()), lastUpdated },
    { name: 'Machines', count: null, severity: 'HIGH', lastUpdated }
  ];
  // overshoot length to improve chances of including all array items
  const size = randInt(1, all.length + 1);
  return all.slice(0, size);
};


export default {
  subscriptionDestination: '/user/queue/administration/context/flagging',
  requestDestination: '/ws/administration/context/flagging',
  page(frame, sendMessage) {

    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { filter } = bodyParsed;

    // The filter should be an array of `{ field: String, values: [] }` pairs, where each `field` is an entity type,
    // and each `values` is an array of entity ids.
    // Transform that into a simple 1-D array of `{ type: String, id: String }` pairs.
    const entities = [];
    (filter || []).forEach((condition) => {
      const { field: type, values } = condition;
      (values || []).forEach((id) => {
        entities.push({ type, id });
      });
    });

    // Create batches of responses. Each one will be just 1 record at a time, for easy testing.
    const batches = [];
    (entities || []).forEach(function(entity) {
      const { type, id } = entity;
      const records = entityRecords();
      records.forEach(function(record) {
        batches.push({
          type,
          id,
          record
        });
      });
    });

    // Send each batch out after a delay.
    const delayBetweenBatches = 50;
    const sendBatch = (index) => {
      return function() {
        const { type, id, record } = batches[index];
        const data = {};
        data[id] = {
          type,
          data: [ record ]
        };
        sendMessage({
          data,
          meta: {
            // Send `complete: true` for last batch, otherwise `complete: false`.
            complete: index === batches.length - 1
          }
        });
      };
    };

    for (let i = 0; i < batches.length; i++) {
      setTimeout(
        sendBatch(i),
        i * delayBetweenBatches
      );
    }
  }
};
