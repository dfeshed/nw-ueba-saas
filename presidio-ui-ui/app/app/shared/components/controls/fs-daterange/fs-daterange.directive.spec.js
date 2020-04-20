describe('fs-daterange.directive', function () {
    'use strict';

    var $compile;
    var $rootScope;
    var $scope;
    var element;
    var utils;
    var config;

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module('fsTemplates');
        // Load required Modules
        module('Fortscale.shared.components.fsDateRange');
    });

    beforeEach(inject(function (_$compile_, _$rootScope_, _utils_, _config_) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        utils = _utils_;
        config = _config_;
    }));


    describe('Interface', function () {

        it('should throw TypeError when fetchStateDelegate is not a function', function () {
            function test () {
                $scope = $rootScope.$new();
                element = $compile('<fs-date-range date-range-id="dr1" ' +
                    'fetch-state-delegate="::\'not a function\'" ' +
                    'update-state-delegate="::overview.setRangeState">' +
                    '</fs-date-range>')($scope);
                $scope.$digest();
            }

            expect(test).toThrowError(TypeError,
                'fsDateRange.directive: FsDateRangeController: ' +
                'If fetchStateDelegate is provided, it must be a function.');
        });

        it('should throw TypeError when updateStateDelegate is not a function', function () {
            function test () {
                $scope = $rootScope.$new();
                element = $compile('<fs-date-range date-range-id="dr1" ' +
                    'update-state-delegate="::\'not a function\'"  >' +
                    '</fs-date-range>')($scope);
                $scope.$digest();
            }

            expect(test).toThrowError(TypeError,
                'fsDateRange.directive: FsDateRangeController: ' +
                'If updateStateDelegate is provided, it must be a function.');
        });

        it('should place fetchStateDelegate function on the controller ' +
            'when passed in the markup', function () {
            $scope.fetchStateDelegate = angular.noop;

            element = $compile('<fs-date-range date-range-id="dr1" ' +
                'fetch-state-delegate="fetchStateDelegate"></fs-date-range>')($scope);

            $scope.$digest();
            var controller = element.controller('fsDateRange');

            expect(controller.fetchStateDelegate).toBe(angular.noop);
        });

        it('should place updateStateDelegate function on the controller ' +
            'when passed in the markup', function () {
            $scope.updateStateDelegate = angular.noop;
            $scope.fetchStateDelegate = angular.noop;

            element = $compile('<fs-date-range date-range-id="dr1" ' +
                'update-state-delegate="updateStateDelegate"' +
                'fetch-state-delegate="fetchStateDelegate"></fs-table>')($scope);

            $scope.$digest();
            var controller = element.controller('fsDateRange');

            expect(controller.updateStateDelegate).toBe(angular.noop);
        });

        it('should place date-range-id on the controller ' +
            'when passed in the markup', function () {
            $scope.updateStateDelegate = angular.noop;
            $scope.fetchStateDelegate = angular.noop;

            var dateRangeId = "dr1";
            element = $compile('<fs-date-range date-range-id="' + dateRangeId +
                '" update-state-delegate="updateStateDelegate"' +
                'fetch-state-delegate="fetchStateDelegate"></fs-table>')($scope);

            $scope.$digest();
            var controller = element.controller('fsDateRange');

            expect(controller.dateRangeId).toBe(dateRangeId);
        });

    });

    describe('Instance', function () {

        var element;
        var ctrl;

        beforeEach(function () {
            $scope.updateStateDelegate = angular.noop;
            $scope.fetchStateDelegate = angular.noop;

            var dateRangeId = "dr1";
            element = $compile('<fs-date-range date-range-id="' + dateRangeId +
                '" update-state-delegate="updateStateDelegate"' +
                'fetch-state-delegate="fetchStateDelegate"></fs-table>')($scope);

            $scope.$digest();
            ctrl = element.controller('fsDateRange');
        });

        describe('dependencies', function () {
            it('should have $element on the controller instance', function () {
                expect(ctrl.utils).toBe(utils);
            });
            it('should have $scope on the controller instance', function () {
                expect(ctrl.$scope).toBeDefined();
            });
            it('should have filter on the controller instance', function () {
                expect(ctrl.$filter).toBeDefined();
            });
        });

        describe('public methods', function () {

            describe('setLast7Days', function () {
                it('should invoke utils.date.getMoment', function () {
                    spyOn(ctrl.utils.date, 'getMoment').and.callThrough();

                    ctrl.setLast7Days();

                    expect(ctrl.utils.date.getMoment).toHaveBeenCalledWith('now');
                });

                it('should set end time', function () {
                    var date = new Date('2015', '1', '2');
                    spyOn(ctrl.utils.date, 'getMoment').and.returnValue({
                        toDate: function () {
                            return date;
                        },
                        subtract: function () {
                            return this;
                        }
                    });

                    ctrl.setLast7Days();

                    expect(ctrl.endTime).toBe(date);
                });

                it('should set start time', function () {
                    var date = new Date('2015', '1', '10');
                    var before7DaysDate = new Date('2015', '1', '3');

                    spyOn(ctrl.utils.date, 'getMoment').and.returnValue({
                        toDate: function () {
                            return date;
                        },
                        subtract: function () {
                            return {
                                toDate: function () {
                                    return before7DaysDate;
                                }
                            };
                        }
                    });

                    ctrl.setLast7Days();

                    expect(ctrl.startTime).toBe(before7DaysDate);
                });

            });

            describe('setLastMonth', function () {
                it('should invoke utils.date.getMoment', function () {
                    spyOn(ctrl.utils.date, 'getMoment').and.callThrough();

                    ctrl.setLastMonth();

                    expect(ctrl.utils.date.getMoment).toHaveBeenCalledWith('now');
                });


                it('should set end time', function () {
                    var date = new Date('2015', '1', '2');
                    spyOn(ctrl.utils.date, 'getMoment').and.returnValue({
                        toDate: function () {
                            return date;
                        },
                        subtract: function () {
                            return this;
                        }
                    });

                    ctrl.setLastMonth();

                    expect(ctrl.endTime).toBe(date);
                });

                it('should set start time', function () {
                    var date = new Date('2015', '1', '10');
                    var before30DaysDate = new Date('2015', '12', '10');

                    spyOn(ctrl.utils.date, 'getMoment').and.returnValue({
                        toDate: function () {
                            return date;
                        },
                        subtract: function () {
                            return {
                                toDate: function () {
                                    return before30DaysDate;
                                }
                            };
                        }
                    });

                    ctrl.setLastMonth();

                    expect(ctrl.startTime).toBe(before30DaysDate);
                });


            });

            describe('isStartTimeAndEndTimeOnDifferentDays', function () {
                it('should return true', function () {

                    ctrl.startTime = new Date('2015', '1', '10', '5');
                    ctrl.endTime = new Date('2015', '1', '10', '1');
                    expect(ctrl.isStartTimeAndEndTimeOnDifferentDays()).toBe(false);
                });

                it('should return false', function () {

                    ctrl.startTime = new Date('2015', '12', '10', '1');
                    ctrl.endTime = new Date('2015', '1', '10', '1');
                    expect(ctrl.isStartTimeAndEndTimeOnDifferentDays()).toBe(true);
                });

            });


        });

        describe('watchers', function () {

            describe('_fetchStateDelegateWatchAction', function () {

                var toDateMock, startOfMock;
                var isStringMock;
                var newVal;
                var startTimeUnix, endTimeUnix;

                beforeEach(function () {

                    // May 22 2015 to June 22 2015
                    newVal = '1432252800,1435017599';
                    startTimeUnix = '1432252800';
                    endTimeUnix = '1435017599';

                    toDateMock = jasmine.createSpy('toDateMock');
                    startOfMock = jasmine.createSpy('startOfMock').and.returnValue({
                        toDate: toDateMock
                    });

                    spyOn(ctrl.utils.date, 'getMoment').and.returnValue({
                        toDate: toDateMock,
                        startOf: startOfMock
                    });

                    isStringMock = spyOn(angular, 'isString').and.callThrough();
                });

                it('should do nothing if newVal is not a string', function () {
                    isStringMock.and.returnValue(false);
                    ctrl._fetchStateDelegateWatchAction(newVal);
                    expect(ctrl.utils.date.getMoment).not.toHaveBeenCalled();
                });

                it('should do nothing if startTimeUnix is not equal to ctrl.startTimeUnix ' +
                    'and endTimeUnix is not the same as ctrl.endTimeUnix', function () {
                    isStringMock.and.returnValue(true);
                    ctrl.startTimeUnix = startTimeUnix;
                    ctrl.endTimeUnix = endTimeUnix;
                    ctrl._fetchStateDelegateWatchAction(newVal);
                    expect(ctrl.utils.date.getMoment).not.toHaveBeenCalled();
                });

                it('should invoke getMoment with startTimeUnix', function () {
                    ctrl._fetchStateDelegateWatchAction(newVal);
                    expect(ctrl.utils.date.getMoment).toHaveBeenCalledWith(startTimeUnix);
                });

                it('should invoke getMoment with endTimeUnix', function () {
                    ctrl._fetchStateDelegateWatchAction(newVal);
                    expect(ctrl.utils.date.getMoment).toHaveBeenCalledWith(endTimeUnix);
                });

                it('should invoke startOf with "day"', function () {
                    ctrl._fetchStateDelegateWatchAction(newVal);
                    expect(startOfMock).toHaveBeenCalledWith('day');
                });

                it('should invoke toDate twice', function () {
                    ctrl._fetchStateDelegateWatchAction(newVal);
                    expect(toDateMock.calls.count()).toBe(2);
                });

            });

        });

        describe('integration', function () {

            describe('_startTimeEndTimeWatchAction integration', function () {

                it('should not call updateStateDelegate', function () {
                    ctrl.updateStateDelegate = null;
                    spyOn(ctrl, 'updateStateDelegate');
                    ctrl._startTimeEndTimeWatchAction('a', 'b');
                    $scope.$digest();

                    expect(ctrl.updateStateDelegate).not.toHaveBeenCalled();
                });

                it('should call updateStateDelegate with original start/end time ' +
                    'when config.alwaysUTC=false', function () {
                    var originalAlwaysUTC = config.alwaysUtc;
                    config.alwaysUtc = false;

                    var startTime = new Date(2015, 1, 2);
                    var endTime = new Date(2015, 1, 3);

                    //Expected end time is end of Jan 3, 2015
                    var expectedEndTime = new Date(new Date(2015, 1, 4).valueOf() - 1);

                    var newValues = [
                        startTime,
                        endTime
                    ];

                    ctrl.dateRangeId = "id";
                    ctrl.updateStateDelegate = null;
                    spyOn(ctrl, 'updateStateDelegate');
                    ctrl._startTimeEndTimeWatchAction(newValues, 'a');

                    $scope.$digest();

                    var expectedTimes = '' + Math.floor(startTime.valueOf() / 1000) + ',' +
                        Math.floor(expectedEndTime.valueOf() / 1000);

                    var expectedObj = {
                        id: 'id', type: 'DATA', value: expectedTimes, immediate: false
                    };

                    expect(angular.equals(
                        ctrl.updateStateDelegate.calls.argsFor(0)[0],
                        expectedObj
                    )).toBe(true);

                    config.alwaysUtc = originalAlwaysUTC;

                });

                it('should call updateStateDelegate with UTC start/end time ' +
                    'when config.alwaysUTC=true', function () {
                    var originalAlwaysUTC = config.alwaysUtc;
                    config.alwaysUtc = true;

                    var startTime = new Date(2015, 1, 2);
                    var endTime = new Date(2015, 1, 3);

                    var startTimeInUTC = new Date(Date.UTC(2015, 1, 2));
                    //var endTimeInUTC = new Date(Date.UTC(2015, 1, 3));

                    //Expected end time is end of Jan 3, 2015, in UTC
                    var expectedEndTime = new Date(new Date(Date.UTC(2015, 1, 4).valueOf() - 1));
                    var newValues = [
                        startTime,
                        endTime
                    ];
                    ctrl.dateRangeId = "id";
                    ctrl.updateStateDelegate = null;
                    spyOn(ctrl, 'updateStateDelegate');
                    ctrl._startTimeEndTimeWatchAction(newValues, 'a');

                    $scope.$digest();

                    var expectedTimes = '' + Math.floor(startTimeInUTC.valueOf() / 1000) + ',' +
                        Math.floor(expectedEndTime.valueOf() / 1000);

                    var expectedObj = {
                        id: 'id', type: 'DATA', value: expectedTimes, immediate: false
                    };

                    expect(angular.equals(
                        ctrl.updateStateDelegate.calls.argsFor(0)[0],
                        expectedObj
                    )).toBe(true);

                    config.alwaysUtc = originalAlwaysUTC;

                });

            });

            describe('_prepareSelectedDate', function () {

                it('should throw exception if time is not defined ', function () {

                    expect(ctrl._prepareSelectedDate).toThrowError(TypeError,
                        'fsDateRange.directive: FsDateRangeController: ' +
                        'time must be defined and be an Object of type Date');
                });

                it('should throw exception if time is not Date object', function () {

                    var test = function (){
                        ctrl._prepareSelectedDate('some string');
                    };

                    expect(test).toThrowError(TypeError,
                        'fsDateRange.directive: FsDateRangeController: ' +
                        'time must be defined and be an Object of type Date');
                });

                it('should call ctrl.utils.date.getMoment with date s MM/DD/YYYY, null, MM/DD/YYYY',
                    function () {

                    var time =  new Date('2015', '11', '10', '1');
                    spyOn(ctrl.utils.date, 'getMoment');

                    ctrl._prepareSelectedDate(time);

                    expect(ctrl.utils.date.getMoment).
                        toHaveBeenCalledWith('12/10/2015', null, 'MM/DD/YYYY');
                });

            });

        });
    });
});
