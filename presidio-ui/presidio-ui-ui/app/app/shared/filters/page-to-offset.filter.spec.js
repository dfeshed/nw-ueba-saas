describe('pageToOffset.filter', function () {
    'use strict';

    var pageToOffsetFilter;

    beforeEach(module('Fortscale.shared.services.assert'));
    beforeEach(module('Fortscale.shared.filters'));

    beforeEach(inject(function (_pageToOffsetFilter_) {
        pageToOffsetFilter = _pageToOffsetFilter_;
    }));

    it('should be defined', function () {

        expect(pageToOffsetFilter).toBeDefined();
    });

    it('should throw TypeError if pageNum is parsed to NaN', function () {
        function testFunc () {
            pageToOffsetFilter('notanumber');
        }
        expect(testFunc)
            .toThrowError(TypeError,
            'Fortscale.shared.filters: pageToOffset: pageNumStr argument must be a number.');
    });

    it('should throw ReferenceError when pageSizeStr is not defined', function () {
        function testFunc () {
            pageToOffsetFilter(10);
        }
        expect(testFunc)
            .toThrowError(ReferenceError,
            'Fortscale.shared.filters: pageToOffset: pageSize argument must be provided.');
    });

    it('should throw ReferenceError when pageSizeStr is not a number', function () {
        function testFunc () {
            pageToOffsetFilter(10, 'notanumber');
        }
        expect(testFunc)
            .toThrowError(TypeError,
            'Fortscale.shared.filters: pageToOffset: pageSize argument must be a number.');
    });

    it('should throw RangeError when pageSizeStr evaluates to 0', function () {
        function testFunc () {
            pageToOffsetFilter(10, '0');
        }
        expect(testFunc)
            .toThrowError(RangeError,
            'Fortscale.shared.filters: pageToOffset: pageSize argument must not be equal to 0.');
    });

    it('should return undefined if pageNumStr is undefined', function () {

        expect(pageToOffsetFilter()).toBe(undefined);
    });

    it('should return the value of (pageNum-1)*pageSize', function () {

        expect(pageToOffsetFilter('10', '10')).toBe(90);
        expect(pageToOffsetFilter(10, 10)).toBe(90);
    });
});
