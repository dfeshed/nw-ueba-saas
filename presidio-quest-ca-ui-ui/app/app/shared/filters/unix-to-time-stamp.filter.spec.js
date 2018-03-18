describe('unixToTimeStamp.filter', function () {
    'use strict';

    var unixToTimeStampFilter;

    beforeEach(module('Fortscale.shared.filters'));

    beforeEach(inject(function (_unixToTimeStampFilter_) {
        unixToTimeStampFilter = _unixToTimeStampFilter_;
    }));

    it('should be defined', function () {

        expect(unixToTimeStampFilter).toBeDefined();
    });


    it('should return the provided value if value is falsy', function () {
        expect(unixToTimeStampFilter(undefined)).toBe(undefined);
        expect(unixToTimeStampFilter(null)).toBe(null);
        expect(unixToTimeStampFilter('')).toBe('');
    });

    it('should take a csv and convert it values and convert each value from ' +
        'unix to time stamp in milliseconds, then convert back to csv', function () {
        expect(unixToTimeStampFilter('1000000000,2000000000'))
        .toBe('1000000000000,2000000000000');
    });

    it('should throw if any of the values derived from the csv are not 10 chars unix ' +
        'time stamp', function () {

        var testFunc = function () {
            return unixToTimeStampFilter('100,2000000000');
        };

        expect(testFunc).toThrowError(RangeError,
            'unixToTimeStamp.filter: csv must be of 10 ' +
            'chars long unix time stamps. "100" is not.');
    });
});
