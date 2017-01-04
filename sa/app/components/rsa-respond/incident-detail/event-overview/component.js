import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  inject: {
    service
  },
  isPresent,
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

  @alias('event.source.device') sourceDevice: null,
  @alias('event.destination.device') destinationDevice: null,
  @alias('event.enrichment.whois') whois: null,
  @alias('event.detector') detector: null,

  @computed('event')
  isEventLoaded: (event) => !isNone(event),

  /**
   * @name serviceId
   * @description: Required route query param for Investigation page. Service ID is retrieved by matching service's name
   * that is known to the event (eg. Concetrator A) to a name in the services container that is backed by core-services.
   * @private
   */
  @computed('services', 'event.event_source_id')
  serviceId: (services, eventSourceId) => {
    let serviceId = null;
    if (isPresent(services) && isPresent(eventSourceId)) {
      const service = services.findBy('displayName', eventSourceId);
      serviceId = isNone(service) ? null : service.id;
    }
    return serviceId;
  },

  /**
   * @name isLinkable
   * @description: Determine if it is possible to create a link to Investigate page.
   * @private
   */
  @computed('serviceId', 'incident.created')
  isLinkable: (serviceId, created) => isPresent(serviceId) && isPresent(created),

  /**
   * @name startTime
   * @description: Required route query param for Investigation page. Value is set to 10 minutes before incident was
   * created. Default value is 0.
   * @private
   */
  @computed('isLinkable', 'incident.created')
  startTime: (isLinkable, created) => isLinkable ? Math.max(Math.round(created / 1000 - 600), 0) : 0,

  /**
   * @name endTime
   * @description: Required route query param for Investigation page. Value is set to 10 minutes after incident was
   * created. Default value is 0.
   * @private
   */
  @computed('isLinkable', 'incident.created')
  endTime: (isLinkable, created) => isLinkable ? Math.max(Math.round(created / 1000 + 600), 0) : 0,

  /**
   * @name investigateRouteParams
   * @description: Array composed of required route query params for Investigation page.
   * @private
   */
  @computed('isLinkable', 'serviceId', 'startTime', 'endTime')
  investigateRouteParams: (isLinkable, serviceId, startTime, endTime) =>
    isLinkable ? [serviceId, startTime, endTime] : [],

  /**
   * @name sourceIpQuery
   * @description: Route params to Investigate page to obtain information based on source IP.
   * @public
   */
  @computed('investigateRouteParams', 'event.ip_source')
  sourceIpQuery: (investigateRouteParams, ipSource) =>
    (isPresent(investigateRouteParams) && isPresent(ipSource)) ?
      investigateRouteParams.slice().addObject(`ip.src=${ipSource}`).join('/') : null,

  /**
   * @name destIpQuery
   * @description: Route params to Investigate page to obtain information based on destination IP.
   * @public
   */
  @computed('investigateRouteParams', 'event.ip_dst')
  destIpQuery: (investigateRouteParams, ipDst) =>
    (isPresent(investigateRouteParams) && isPresent(ipDst)) ?
      investigateRouteParams.slice().addObject(`ip.dst=${ipDst}`).join('/') : null,

  /**
   * @name aliasHostQuery
   * @description: Route params to Investigate page to obtain information based on alias host.
   * @public
   */
  @computed('investigateRouteParams', 'event.alias_host')
  aliasHostQuery: (investigateRouteParams, aliasHost) =>
    (isPresent(investigateRouteParams) && isPresent(aliasHost)) ?
      investigateRouteParams.slice().addObject(`alias.host=${aliasHost}`).join('/') : null,

  /**
   * @name detectorDeviceIpQuery
   * @description: Route params to Investigate page to obtain information based on detector's device IP.
   * @public
   */
  @computed('investigateRouteParams', 'event.detector.ip_address')
  detectorDeviceIpQuery: (investigateRouteParams, detectorDeviceIpAddress) =>
    (isPresent(investigateRouteParams) && isPresent(detectorDeviceIpAddress)) ?
      investigateRouteParams.slice().addObject(`device.ip=${detectorDeviceIpAddress}`).join('/') : null,

  @computed('event.enrichment')
  scoreBadges(enrichment) {
    const scoreBadges = [];

    if (!isNone(enrichment)) {

      const badgeScoreConfig = [
        { badgeName: 'beaconBehavior', metaKeys: ['smooth.smooth_beaconing_score', 'rsa_analytics_http-packet_c2_beaconing_score', 'rsa_analytics_http-log_c2_beaconing_score'] },
        { badgeName: 'domainAge', metaKeys: ['whois.age_score', 'rsa_analytics_http-packet_c2_whois_age_score', 'rsa_analytics_http-log_c2_whois_age_score'] },
        { badgeName: 'expiringDomain', metaKeys: ['whois.validity_score', 'rsa_analytics_http-packet_c2_whois_validity_score', 'rsa_analytics_http-log_c2_whois_validity_score'] },
        { badgeName: 'noReferrers', metaKeys: ['domain.referer_ratio_score', 'rsa_analytics_http-packet_c2_referer_ratio_score', 'rsa_analytics_http-log_c2_referer_ratio_score'] },
        { badgeName: 'rareUserAgent', metaKeys: ['domain.ua_ratio_score', 'rsa_analytics_http-packet_c2_ua_ratio_score', 'rsa_analytics_http-log_c2_ua_ratio_score'] },
        { badgeName: 'rareDomain', metaKeys: ['domain.referer_score', 'rsa_analytics_http-packet_c2_referer_score', 'rsa_analytics_http-log_c2_referer_score'] },
        { badgeName: 'manyServers', metaKeys: ['rsa_analytics_uba_winauth_highserverscore_score'] },
        { badgeName: 'manyNewServers', metaKeys: ['rsa_analytics_uba_winauth_newserverscore_score'] },
        { badgeName: 'manyNewDevices', metaKeys: ['rsa_analytics_uba_winauth_newdevicescore_score'] },
        { badgeName: 'passTheHash', metaKeys: ['rsa_analytics_uba_winauth_newdeviceservice_score'] },
        { badgeName: 'manyFailedLogins', metaKeys: ['rsa_analytics_uba_winauth_failedserversscore_score'] },
        { badgeName: 'rareLogonType', metaKeys: ['rsa_analytics_uba_winauth_logontypescore_score'] },
        { badgeName: 'manyLoginFailures', metaKeys: ['rsa_analytics_uba-cisco_vpn_smoothloginfiluresscore_score'] },
        { badgeName: 'rareDevice', metaKeys: ['rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score'] },
        { badgeName: 'rareLocation', metaKeys: ['rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score'] },
        { badgeName: 'rareServiceProvider', metaKeys: ['rsa_analytics_uba-cisco_vpn_smoothrareispscore_score'] },
        { badgeName: 'newServiceProvider', metaKeys: ['rsa_analytics_uba-cisco_vpn_smoothnewispscore_score'] }
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

  @computed('event')
  normalizedMeta(event) {
    const rootNode = { title: '', key: 'meta', showHeader: false };
    const normalizedMeta = this._normalizeMetaElement(event, rootNode, [ rootNode ]);
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
