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
 * @name _SOURCES_LIST
 * @description Private array composed of all avialble sources.
 * @private
 */
const _SOURCES_LIST = [
  'Event Stream Analysis',
  'Event Streaming Analytics',
  'ECAT',
  'Malware Analysis',
  'Reporting Engine',
  'Security Analytics Investigator',
  'Web Threat Detection'
];

/**
 * @name _SOURCES_NAME_EXCEPTION
 * @description Private JSON object composed of source names and properties that are used
 * to beautify source names for end-user.
 * short - property can be used to display abbreviated source names (eg. in table columns)
 * @private
 */
const _SOURCES_NAME_EXCEPTION = {
  'ECAT': {
    'short': 'ECAT'
  }
};

export default {

  /**
   * @name sourceLongNames
   * @description returns an array with a complete list of sources
   * @public
   */
  sourceLongNames() {
    return _SOURCES_LIST;
  },

  /**
   * @name sourceShortName
   * @param source a String value of original name of the source as it is known on the server (see _SOURCE_MAP)
   * @description returns a parametrized shortname for each source, if there is no configuration for the source, the
   * initials are returned
   * @public
   */
  sourceShortName(source) {
    return _SOURCES_NAME_EXCEPTION[source] ? _SOURCES_NAME_EXCEPTION[source].short : source.match(/\b\w/g).join('');
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
    const normalizedCategories = [];

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
