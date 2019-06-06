import { camelize } from '@ember/string';

/**
 * Common to all the query in process analysis
 * @param agentId
 * @returns {*[]}
 * @private
 */
const _commonFilter = (agentId) => {
  const query = [
    {
      meta: 'agent.id',
      operator: '=',
      value: `'${agentId}'`
    },
    {
      meta: 'device.type',
      operator: '=',
      value: '\'nwendpoint\''
    },
    {
      value: '(category=\'Process Event\' || category = \'Registry Event\' || category = \'File Event\' || category = \'Network Event\' || category = \'Console Event\')'
    }
  ];
  return query;
};
/**
 * Create Porcess Filter
 * @returns {{meta: string, operator: string, value: string}}
 * @private
 */
const _createProcessFilter = () => {
  return {
    meta: 'action',
    operator: '=',
    value: '\'createProcess\''
  };
};

/**
 * Process Id filter
 * @param pid
 * @param isParentAndChild
 * @returns {{value: string}}
 * @private
 */
const _processIdFilter = (pid, isParentAndChild) => {
  if (isParentAndChild) {
    return { value: `(process.vid.src = '${pid}' || process.vid.dst = '${pid}')` };
  } else {
    return { value: `(process.vid.src = '${pid}')` };
  }
};

/**
 * Returns query node
 * @param input
 * @param selectedVid
 * @param type
 * @param filters
 * @returns {{agentId: *, endTime: *, startTime: *, queryTimeFormat: string, serviceId: *, metaFilter: {conditions}}}
 * @public
 */
export const getQueryNode = function(input = {}, selectedVid, type, filters) {
  const { et, st, sid, aid: agentId, vid } = input;

  let processId = vid;

  if (vid) {
    processId = selectedVid;
  }

  const queryNode = {
    agentId,
    endTime: et,
    startTime: st,
    queryTimeFormat: 'DB',
    serviceId: sid,
    metaFilter: getMetaFilterFor(type, agentId, processId, filters)
  };
  return queryNode;
};


/**
 * Construct the meta filter conditions based on the filterFor property
 * Filter For includes:
 * PARENT_CHILD: While constructing the tree for given process we need to fetch both it's children and parent
 * CHILD: For a given process need get all the children
 * FILTER: Apply the filter in events table
 *
 * To construct a process tree need to get all the createProcess event for given host and a process and it should include
 * only endpoint related events. Always query will contain fixed set of meta, agent.id, action, type and file name
 *
 * agent.id -> for a given host
 * action -> to get only createProcess events
 * device.type -> to get only endpoint related events
 * fileName.src -> selected process
 *
 * @public
 *
 */
export const getMetaFilterFor = (filterFor, agentId, pid, filters) => {
  let conditions = _commonFilter(agentId);

  switch (filterFor) {
    case 'PARENT_CHILD':
      conditions = conditions.concat([
        _createProcessFilter(),
        _processIdFilter(pid, true)
      ]);
      break;
    case 'CHILD':
      conditions = conditions.concat([
        _createProcessFilter(),
        _processIdFilter(pid, false)
      ]);
      break;
    case 'FILTER':
      conditions = conditions.concat([
        _processIdFilter(pid, true)
      ]);
      if (filters && filters.length) {
        conditions = conditions.concat(filters);
      }
      break;
  }
  return { conditions };
};

const _hasSha256 = (hash) => {
  return hash && hash.length === 64;
};


/**
 * Event response is in ['key', 'value] formate, So need to convert the event meta into object
 * @param {*} event
 * @returns {*} event
 * @public
 */
export const hasherizeEventMeta = (event) => {
  if (event) {
    const { metas } = event;
    if (!metas) {
      return;
    }
    const len = (metas && metas.length) || 0;
    let i;
    for (i = 0; i < len; i++) {
      const meta = metas[i];
      if (meta[0] === 'filename.dst') {
        event.processName = meta[1];
      }
      if (meta[0] === 'agent.id') {
        event.agentId = meta[1];
      }

      if (meta[0] === 'checksum.dst') {
        if (_hasSha256(meta[1])) {
          event.checksumDst = meta[1];
        }
      } else if (meta[0] === 'checksum.src') {
        if (_hasSha256(meta[1])) {
          event.checksumSrc = meta[1];
        }
      } else {
        event[camelize(meta[0])] = meta[1];
      }
    }
    event.childCount = 0;
    event.metas = null;
  }
};
