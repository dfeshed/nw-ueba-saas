/**
 * @file Incident helper utilities
 * @public
 */
import Ember from 'ember';
import { incidentRiskThreshold } from 'sa/incident/constants';

const {
  isArray,
  isNone
} = Ember;


/**
 * @name _SOURCES_MAP
 * @description Private JSON object composed of source names and properties that are used
 * to beautify source names for end-user.
 * 1. short property can be used to display abbreviated source names (eg. in table columns)
 * 2. long property can be used in any scenario where full source name should be displayed (eg. in rsa-form-select)
 * @private
 */
const _SOURCES_MAP = {
  'Event Stream Analysis': {
    'short': 'ESA'
  },
  'Event Streaming Analytics': {
    'short': 'ESA'
  },
  'ecat': {
    'short': 'ECAT',
    'long': 'ECAT'
  },
  'Malware Analysis': {
    'short': 'MA'
  },
  'Reporting Engine': {
    'short': 'RE'
  },
  'Security Analytics Investigator': {
    'short': 'SAI'
  },
  'Web Threat Detection': {
    'short': 'WTD'
  }
};

export default {

  /**
   * @name sourceLongNames
   * @description returns a styalized longname for each source (if any) or its original name
   * @public
   */
  sourceLongNames() {
    let names = [];
    Object.keys(_SOURCES_MAP).forEach((source) => {
      let sourceName = _SOURCES_MAP[source].long ? _SOURCES_MAP[source].long : source;
      names.pushObject({ id: source, name: sourceName });
    });
    return names;
  },

  /**
   * @name sourceShortName
   * @param source Original name of the source as it is known on the server or the ID in the _SOURCE_MAP
   * @description returns a parametrized shortname for each source, if there is no configuration for the source, the
   * initials are returned
   * @public
   */
  sourceShortName(source) {
    return _SOURCES_MAP[source] ? _SOURCES_MAP[source].short : source.match(/\b\w/g).join('');
  },

  /**
   * @name riskScoreToBadgeLevel
   * @description define the badge style [low, medium, high, danger] based on the incident' risk score
   * @public
   */
  riskScoreToBadgeLevel(riskScore) {
    if (riskScore < incidentRiskThreshold.LOW) {
      return 'low';
    } else if (riskScore < incidentRiskThreshold.MEDIUM) {
      return 'medium';
    } else if (riskScore < incidentRiskThreshold.HIGH) {
      return 'high';
    } else {
      return 'danger';
    }
  },

  /**
   * @description transforms a one dimension categories array into a 2 dimension array
   * @public
   */
  normalizeCategoryTags(categoryTags = []) {
    let normalizedCategories = [];

    categoryTags.forEach((category) => {
      let objParent = normalizedCategories.findBy('name', category.parent);

      if (!objParent) {
        objParent = {
          name: category.parent,
          children: []
        };
        normalizedCategories.pushObject(objParent);
      }

      objParent.children.pushObject(category);

    });

    return normalizedCategories;
  },

  /**
   * @description It returns a printable version of a IP array based on the input size:
   * - If zero elements or null reference is passed, it returns a '-'
   * - If the array has 1 element, it returns its value
   * - If more than 1 element is in the array, the size of the array is returned
   * @param array
   * @public
   */
  groupByIp: (ipList) => {
    if (isNone(ipList) || !isArray(ipList) || ipList.length === 0) {
      return '-';
    } else {
      // temporarily hardcoding string `IPs`. It will removed when using another component to display list of IPs
      return ipList.length === 1 ? ipList.get('firstObject') : `(${ ipList.length } IPs)`;
    }
  }
};
