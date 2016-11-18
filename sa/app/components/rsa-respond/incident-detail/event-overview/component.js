import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  inject: {
    service
  },
  isNone,
  isEmpty,
  typeOf,
  get
} = Ember;

export default Component.extend({
  classNames: ['event-overview', 'spacer'],
  layoutService: service('layout'),

  // indicates if the meta panel is visible
  metaVisible: false,
  // true when the panel is extended, otherwise false
  isFullWidth: true,

  // list of meta keys that wont be displayed as part of the meta section title
  normalizedMetaHiddenKeys: ['enrichment'],

  @alias('model.source.device') sourceDevice: null,
  @alias('model.destination.device') destinationDevice: null,
  @alias('model.enrichment.whois') whois: null,

  @computed('model')
  isModelLoaded: (model) => !isNone(model),

  @computed('model.enrichment')
  scoreBadges(enrichment) {
    const scoreBadges = [];

    if (!isNone(enrichment)) {

      const badgeScoreConfig = [
        { badgeName: 'beaconBehavior', metaKeys: ['smooth.smooth_beaconing_score', 'rsa_analytics_http-packet_c2_beaconing_score', 'rsa_analytics_http-log_c2_beaconing_score'] },
        { badgeName: 'domainAge', metaKeys: ['whois.age_score', 'rsa_analytics_http-packet_c2_whois_age_score', 'rsa_analytics_http-log_c2_whois_age_score'] },
        { badgeName: 'expiringDomain', metaKeys: ['whois.validity_score', 'rsa_analytics_http-packet_c2_whois_validity_score', 'rsa_analytics_http-log_c2_whois_validity_score'] },
        { badgeName: 'noReferrers', metaKeys: ['domain.referer_ratio_score', 'rsa_analytics_http-packet_c2_referer_ratio_score', 'rsa_analytics_http-log_c2_referer_ratio_score'] },
        { badgeName: 'rareUserAgent', metaKeys: ['domain.ua_ratio_score', 'rsa_analytics_http-packet_c2_ua_ratio_score', 'rsa_analytics_http-log_c2_ua_ratio_score'] },
        { badgeName: 'rareDomain', metaKeys: ['domain.referer_score', 'rsa_analytics_http-packet_c2_referer_score', 'rsa_analytics_http-log_c2_referer_score'] }
      ];

      badgeScoreConfig.forEach((config) => {
        const score = this._readScoreValue(enrichment, config.metaKeys);
        if (!isNone(score)) {
          scoreBadges.addObject({ key: config.badgeName, score: Math.round(score) });
        }
      });

    }
    return scoreBadges;
  },

  /**
   * @description helper method that, given a list of keys `metaKeys`, returns the first value that is not `null` nor `undefined`
   * @private
   */
  _readScoreValue(enrichment, metaKeys) {
    let score = null;
    for (let index = 0; index < metaKeys.length && isNone(score); index++) {
      const metaKey = metaKeys.objectAt(index);
      const val = get(enrichment, metaKey);
      if (!isNone(val)) {
        score = Math.round(val);
      }
    }
    return score;
  },

  @computed('model')
  normalizedMeta(model) {
    const rootNode = { title: '', key: 'meta', showHeader: false };
    const normalizedMeta = this._normalizeMetaElement(model, rootNode, [ rootNode ]);
    return normalizedMeta;
  },

  /**
   * @name normalizeMetaElement
   * @description: normalize in a single 1 level array all the attributes of `element`
   *  Values are store in 2 different inner objects named `col0` and `col1` the following way:
   *  - First meta value is store in `col0`
   *  - Second meta value is store in `col1`
   *  - Third meta value is store back in `col0`
   *  - Forth meta value is store again in `col1`...
   * @param {Object} element: the element to be normalized
   * @param {Object} parentNode: the parent of `element`
   * @param {Array} normalizedMeta: the destination where the normilized element will be stored
   * @private
   */
  _normalizeMetaElement: (element, parentNode, normalizedMeta, elementIndex = 0) => {
    let colName, elKeyName;
    let colNum = 1;

    // if a meta element has just a value (string, number, etc), we create an object of it.
    if (typeOf(element) !== 'object' && typeOf(element) !== 'array') {
      element = { [elementIndex]: element };
    }

    const keys = Object.keys(element);

    keys.forEach((key) => {

      const value = element[key];
      const type = typeOf(value);

      if (type === 'array') {

        // special case, related_links has its own section.
        if (key !== 'related_links') {
          value.forEach((el, index) => {
            elKeyName = `${ key }`;
            if (value.length > 1) {
              elKeyName = `${ elKeyName } [${ index }]`;
            }
            this._normalizeMetaObject(elKeyName, el, parentNode, normalizedMeta, index);
          });
        }

      } else if (type === 'object') {

        this._normalizeMetaObject(key, value, parentNode, normalizedMeta);

      } else {
        // adding values into 2 columns. The first element goes to col0, the second to col1, the third back to col0 and so on
        colNum = 1 - colNum;
        colName = `col${colNum}`;

        if (isNone(parentNode[colName])) {
          parentNode[colName] = [];
        }
        parentNode[colName].addObject({
          key,
          label: String(key).replace(/_/g, ' '),
          value: String(value)
        });
      }
    });
    return normalizedMeta;
  },

  /**
   * @name _normalizeMetaObject
   * @description normalize a meta object to be displayed in the meta panel
   * @private
   */
  _normalizeMetaObject(metaKey, metaValue, parentMeta, normalizedMeta, arrayElementIndex) {
    const keyName = `${ parentMeta.key }::${ metaKey }`;
    const title = this._createTitle(parentMeta.title, metaKey);

    const newNode = { title, key: keyName, col0: [], showHeader: true };

    normalizedMeta.addObject(newNode);
    this._normalizeMetaElement(metaValue, newNode, normalizedMeta, arrayElementIndex);

    // when the just added object is empty, we remove it to avoid an empty section
    const parentObj = normalizedMeta.findBy('key', keyName);
    if (parentObj.col0.length === 0) {
      normalizedMeta.removeObject(parentObj);
    }
  },

  /**
   * @name _createTitle
   * @description Given a meta key and its parent object, creates the title of the meta section.
   * @private
   */
  _createTitle(parentTitle, metaKey) {
    let delimitor = '';
    let prefix = '';
    let title = '';

    if (!isEmpty(parentTitle)) {
      prefix = parentTitle;
    }
    if (!get(this, 'normalizedMetaHiddenKeys').includes(metaKey)) {
      title = metaKey;
    }
    if (!isEmpty(prefix) && !isEmpty(title)) {
      delimitor = ' ';
    }

    return `${ prefix }${ delimitor }${ title }`;
  },

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  badgeStyle: (riskScore) => IncidentHelper.riskScoreToBadgeLevel(riskScore),

  actions: {
    toggleMetaSection() {
      this.toggleProperty('metaVisible');
    },

    closePanel() {
      this.set('isFullWidth', true);
      this.sendAction('closeEventOverviewPanelAction');
    },

    expandCollapsePanel() {
      const isFullWidth = this.toggleProperty('isFullWidth');
      this.sendAction('expandCollapseEventOverviewPanelAction', isFullWidth);
    }
  }
});
