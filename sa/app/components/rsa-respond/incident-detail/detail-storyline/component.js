import Ember from 'ember';
import moment from 'moment';
import computed from 'ember-computed-decorators';


const {
  Component,
  isNone,
  isEmpty,
  isArray,
  run
} = Ember;

export default Component.extend({

  model: null,

  /**
   * @name sources
   * @description config that holds the modelName, sourceName and sourceType mapping
   * for all supported indicators
   * @public
   */
  sources: {
    'C2-Packet': {
      sourceName: 'ATD',
      sourceTypes: ['UEBA', 'Packet']
    },
    'C2-Log': {
      sourceName: 'ATD',
      sourceTypes: ['UEBA', 'Log']
    },
    'UBA-WinAuth': {
      sourceName: 'UEBA',
      sourceTypes: ['UEBA']
    },
    'ESA': {
      sourceName: 'ESA',
      sourceTypes: ['ESA']
    },
    'ModuleIOC': {
      sourceName: 'ENDPOINT',
      sourceTypes: ['ENDPOINT']
    }
  },

  /**
   * @name firstIndicatorTime
   * @description time when first indicator was created
   * @public
   */
  firstIndicatorTime: null,

  /**
   * @name lastIndicatorTime
   * @description time when last indicator was created
   * @public
   */
  lastIndicatorTime: null,

  /**
   * @name indicators
   * @description returns all indicators that has to be populated
   * @public
   */
  @computed('i18n', 'model.[]')
  indicators(i18n, allIndicators) {
    if (isEmpty(allIndicators)) {
      return [];
    }
    const data = [];
    const nLength = allIndicators.length;
    const indicatorTimeFrame = allIndicators[ 0 ].indicator.alert.timestamp;
    run.next(() => {
      this.setProperties({
        'firstIndicatorTime': indicatorTimeFrame,
        'lastIndicatorTime': allIndicators[nLength - 1].indicator.alert.timestamp
      });
    });
    let previousAlertTime = moment(indicatorTimeFrame);

    allIndicators.forEach((indicators, k) => {
      // destructure indicator, matched and lookup
      const { indicator, matched, lookup } = indicators;
      // destructure signature_id and model_name
      const { signature_id: signatureId } = indicator.originalHeaders;
      let { model_name: modelName } = indicator.originalHeaders;
      if (isNone(modelName)) {
        modelName = signatureId;
      }
      const { alert } = indicator;
      const sources = this.get('sources');
      const indicatorSources = sources[modelName];

      if (!isEmpty(indicatorSources)) {
        // get the sourceName and sourcetype from the config based on modelName
        const { sourceName, sourceTypes } = indicatorSources;
        indicator.modelName = modelName;
        // group will be set to "0" for catalyst
        indicator.catalyst = (indicators.group === '0');
        indicator.sourceName = sourceName;
        indicator.sourceTypes = sourceTypes;

        if (sourceName) {
          const currAlertTime = moment(alert.timestamp);
          // show the date bar only if the current date is different
          // from the previous indicator's date
          if (k > 0) {
            const currAlertTime = moment(alert.timestamp);
            if (previousAlertTime.isSame(currAlertTime, 'day')) {
              alert.hideDate = true;
            }
          }
          previousAlertTime = currAlertTime;
          // indicator.matched is an ordered set, in this order
          // [User, Host, Domain, Source ip, File hash]
          const objMatched = {};
          if (isArray(matched)) {
            [objMatched.user, objMatched.host, objMatched.domain, objMatched.srcIp, objMatched.fileHash] = matched;
          }
          const objLookup = [];
          const lookupforHost = (lookup && lookup[objMatched.host]);
          // populate the lookup for all non-catalyst indicators
          if (lookupforHost && indicator.catalyst === false) {
            lookupforHost.forEach((item) => {
              const [, value, lookupType] = item;
              if (lookupType === 'ip2host') {
                const lookupDetails = {
                  title: i18n.t('incident.details.storyline.lookup.ip2host'),
                  source: objMatched.host,
                  dest: value
                };
                objLookup.push(lookupDetails);
              }
            });
            indicator.lookup = objLookup;
          }
          data.push(indicator);
        }
      }
    });
    return data;
  }
});

