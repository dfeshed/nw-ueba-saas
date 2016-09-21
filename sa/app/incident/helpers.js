/**
 * @file Incident helper utilities
 * @public
 */
import { incidentRiskThreshold } from 'sa/incident/constants';

const _SOURCES_MAP = [
  {
    test(source) {
      return String(source).match(/event stream analysis/i);
    },
    short() {
      return 'ESA';
    }
  },
  {
    test(source) {
      return String(source).match(/ecat/i);
    },
    short() {
      return 'ECAT';
    }
  }];

export default {
  /**
   * @name sourceShortName
   * @description returns a parametrized shortname for each source, if there is no configuration for the source, the
   * initials are returned.
   * @public
   */
  sourceShortName(source) {
    let matchedRule = _SOURCES_MAP.find((rule) => rule.test(source));
    return matchedRule ? matchedRule.short(source) : source.match(/\b\w/g).join('');
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
  }
};
