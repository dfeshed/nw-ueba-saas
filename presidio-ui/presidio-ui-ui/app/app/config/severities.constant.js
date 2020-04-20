(function () {
    'use strict';

    /**
     *
     * @param {string} id
     * @param {string} displayName
     * @param {string} displayChar
     * @param {string} color
     * @param {Array<number>} scoreRange
     * @constructor
     */
    function Severity (id, displayName, displayChar, color, scoreRange) {
        this.id = id;
        this.displayName = displayName;
        this.displayChar = displayChar;
        this.color = color;
        this.scoreRange = scoreRange;
    }

    var SEVERITIES;

    /**
     *
     * @param {string} id
     * @returns {Severity || null}
     */
    function getById (id) {
        var severity = _.filter(SEVERITIES, {id: id});
        return severity ? severity[0] : null;
    }

    /**
     *
     * @param {number} score
     * @returns {Severity || null}
     */
    function getByScore (score) {
        var severity = _.filter(SEVERITIES, function (severity) {
            return score >= severity.scoreRange[0] && score <=  severity.scoreRange[1];
        });
        return severity ? severity[0] : null;
    }

    function getList () {
        return _.map(SEVERITIES);
    }


    SEVERITIES = [
        new Severity('critical', 'Critical', 'C', '#F36F63', [90, 100]),
        new Severity('high', 'High', 'H', '#E99848', [75, 89]),
        new Severity('medium', 'Medium', 'M', '#F2C446', [50, 74]),
        new Severity('low', 'Low', 'L', '#B0C75E', [0, 49])
    ];

    SEVERITIES.getById = getById;
    SEVERITIES.getByScore = getByScore;
    SEVERITIES.getList = getList;

    angular.module('Config')
        .constant('SEVERITIES', SEVERITIES);
}());
