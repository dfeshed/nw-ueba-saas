import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';
import { C2Enrichment, WinAuthEnrichment } from 'sa/incident/constants';

const {
  Component,
  typeOf,
  isNone
} = Ember;

const _scoreThreshold = 50;

export default Component.extend({
  tagName: '',

  /**
   * @name badgeScoreConfig
   * @description config that holds the scores for all sub indicators
   * @public
   */
  badgeScoreConfig: {},

  /**
   * @name titleConfig
   * @description config that holds the scores for all main indicators
   * @public
   */
  titleConfig: {},

  /**
   * @description denotes if the current indicator is C2 or WinAuth
   * @public
   */
  indicatorType: null,

  /**
   * @name isCatalyst
   * @description returns if the indicator is catalyst
   * @public
   */
  @alias('indicator.catalyst') isCatalyst: null,

  /**
   * @name enrichment
   * @description holds the list of current enrichments from the indicator
   * @public
   */
  @computed('indicator.alert.events.firstObject')
  enrichment: (event) => event.enrichment,
  /**
   * @name domain
   * @description gets the domain name from the C2 enrichment object
   * @public
   */
  @computed()
  domain() {
    return this.getEnrichmentData('domainName');
  },

  /**
   * @name eventComputer
   * @description gets the computer name from the UEBA enrichment object
   * @public
   */
  @computed()
  eventComputer() {
    return this.getEnrichmentData('eventComputer');
  },

  /**
   * @name isC2Packet
   * @description bool to indicate if the C2 indicator is packet or log.
   * returns true if it is packet.
   * @public
   */
  @computed('indicatorType', 'indicator.modelName')
  isC2Packet: (indicatorType, modelName) => (indicatorType === 'c2' && modelName === 'C2-Packet'),

  /**
   * @name init
   * @description invokes populateIndicators which populates
   * badgeScoreConfig and titleConfig
   * @public
   */
  init() {
    this.populateIndicators();
    this._super(...arguments);
  },

  /**
   * @name getEnrichmentData
   * @description returns the score of the passed enrichment key from titleConfig
   * @public
   */
  getEnrichmentData(key) {
    return this.get('titleConfig')[key];
  },

  /**
   * @name hasDynamici18nString
   * @description returns if the passed 'key' needs dynamic string replacement
   * currently it is whoisAgeScore and expiringDomain
   * @public
   */
  hasDynamici18nString(key) {
    return (key === 'whoisAgeScore' || key === 'expiringDomain');
  },

  /**
   * @name populateIndicators
   * @description populates the badgeScoreConfig and titleConfig
   * @public
   */
  populateIndicators() {
    const badgeScoreConfig = {};
    const titleConfig = {};
    // current indicators enrichments
    const allEnrichments = this.get('enrichment');
    // C2 on WinAuth, etc
    const indicatorType = this.get('indicatorType');
    let type, index, typeOfEnrichments, conditionKey;
    if (indicatorType === 'c2') {
      // check if the indicator is packet or log
      type = this.get('isC2Packet') ? 'packet' : 'log';
      typeOfEnrichments = C2Enrichment;
      // if the enrichment key for 'whoisAvailable' is set to true then we
      // populate 'whoisAgeScore' and 'expiringDomain'. Else the rest of the
      // enrichments will be displayed and these 2 will be dropped off.
      // 'whoisAvailable' set to false means the ESA back-end did not populate
      // the values for enrichment keys 'whoisAgeScore' and 'expiringDomain'
      conditionKey = 'whoisAvailable';
    } else if (indicatorType === 'winauth') {
      typeOfEnrichments = WinAuthEnrichment;
      // if the enrichment key for 'isDeviceExists' is set to true then we
      // populate 'newDeviceScore' and just this sub indicator will be displayed.
      // Else the rest of the enrichments will be displayed
      conditionKey = 'isDeviceExists';
    }
    let displayInfoBasedOnCondition = false;
    for (index in typeOfEnrichments) {
      const scoreKey = typeOfEnrichments[ index ];
      const enrichmentText = index;
      let enrichmentKey = scoreKey.key;
      if (indicatorType === 'c2') {
        // The enrichment keys for packet and log will be
        // rsa_analytics_http-packet_c2_newdomain_age or
        // rsa_analytics_http-log_c2_newdomain_age, respectively
        // C2 keys in our constants file looks like
        // rsa_analytics_http-${type}_c2_newdomain_age
        // we replace ${type} with packet/log depending on indicatorType
        enrichmentKey = enrichmentKey.replace('${type}', type);
      }
      let enrichmentValue = allEnrichments[enrichmentKey];
      // if it is not a title score, populate badgeScoreConfig
      if (isNone(scoreKey.isTitleScore)) {
        // conditionKey will be 'isDeviceExists' for WinAuth or
        // 'whoisAvailable' for C2. displayInfoBasedOnCondition will hold the
        // value returned by the back-end (boolean) for the appropriate enrichment key
        if (index === conditionKey) {
          displayInfoBasedOnCondition = enrichmentValue;
        }
        // storyline shows sub indicators only if score > _scoreThreshold (set to 50)
        if (typeOf(enrichmentValue) === 'number' && enrichmentValue > _scoreThreshold) {
          enrichmentValue = Math.ceil(enrichmentValue);
          let pushEnrichment = false;
          // displayCondition will be set if we need to selectively display the sub indicators
          // based on other enrichment value (isDeviceExists or whoisAvailable)
          const isDisplayConditionSet = typeOf(scoreKey.displayCondition);
          if (indicatorType === 'c2') {
            // if enrichment value for key 'whoisAvailable' is true,
            // push to badgeScoreConfig. 'displayInfoBasedOnCondition' will be set
            // to true only for 'whoisAgeScore' and 'expiringDomain'
            if (isDisplayConditionSet === 'undefined' || displayInfoBasedOnCondition === true) {
              pushEnrichment = true;
            }
          } else if (indicatorType === 'winauth') {
            // if the current key is 'newDeviceScore' and if 'isDeviceExists'
            // is true push 'newDeviceScore' else populate the rest of the keys
            // if ('isDeviceExists') {
            //   show only 'newDeviceScore' enrichment
            // } else {
            //   show rest of the enrichments
            // }
            if (displayInfoBasedOnCondition && index === 'newDeviceScore') {
              pushEnrichment = true;
            } else if (!displayInfoBasedOnCondition && index !== 'newDeviceScore') {
              pushEnrichment = true;
            }
          }
          if (pushEnrichment === true) {
            badgeScoreConfig[enrichmentText] = enrichmentValue;
          }
        }
      } else {
        // titleConfig is populated
        titleConfig[enrichmentText] = (typeOf(enrichmentValue) === 'number') ? Math.ceil(enrichmentValue) : enrichmentValue;
      }
    }
    this.setProperties({
      badgeScoreConfig,
      titleConfig
    });
  }
});
