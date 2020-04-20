(function () {
    'use strict';

    describe('alertFeedback.service', function () {

        var alertFeedback, assert, $http, BASE_URL;

        beforeEach(function () {
            module('Config');
            module('Fortscale.shared.components.alertFeedback');
            module('Fortscale.shared.services.assert');
            module('ui.bootstrap');

            inject(function (_alertFeedback_, _assert_, _$http_, _BASE_URL_) {
                alertFeedback = _alertFeedback_;
                assert = _assert_;
                $http = _$http_;
                BASE_URL = _BASE_URL_;
            });
        });

        describe('Private methods', function () {

            describe('_isValueAllowed', function () {
                var enm;

                beforeEach(function () {
                    enm = {
                        firstProp: 10,
                        secondProp: 20,
                        thirdProp: 30
                    };
                });

                it('should return true when provided with an enum and a value, ' +
                    'where the value is one of the enum\'s properties value', function () {

                    expect(alertFeedback._isValueAllowed(enm, 20)).toBe(true);
                });

                it('should return false when provided with an enum and a value, ' +
                    'where the value is not one of the enum\'s properties value', function () {

                    expect(alertFeedback._isValueAllowed(enm, 40)).toBe(false);
                });
            });

            describe('_verifyAlert', function () {
                var assertIsObjectMock, assertIsStringMock;

                beforeEach(function () {
                    assertIsObjectMock = spyOn(assert, 'isObject');
                    assertIsStringMock = spyOn(assert, 'isString');

                });

                it('should invoke assert.isObject with alert object, "alert", ' +
                    'Alert should be an object', function () {
                    var alert = {
                        id: 'id1',
                        name: 'name1'
                    };

                    alertFeedback._verifyAlert(alert);
                    expect(assertIsObjectMock)
                        .toHaveBeenCalledWith(alert, 'alert',
                        'Alert should be an object');
                });

                it('should invoke assert.isString with alert.id object, "alert id", ' +
                    'Alert must has an ID', function () {
                    var alert = {
                        id: 'id1',
                        name: 333
                    };

                    alertFeedback._verifyAlert(alert);
                    expect(assertIsStringMock)
                        .toHaveBeenCalledWith(alert.id, 'alert id',
                        'Alert must has an ID');
                });

                it('should invoke assert.isString with alert.name object, "alert name", ' +
                    'Alert must has a name', function () {
                    var alert = {
                        id: 'id1',
                        name: 333
                    };

                    alertFeedback._verifyAlert(alert);
                    expect(assertIsStringMock)
                        .toHaveBeenCalledWith(alert.name, 'alert name',
                        'Alert must has a name');
                });

            });

            describe('_approveAlertCloseCallback', function () {
                var closeAlertFunctionMock;
                var model;
                beforeEach(function () {
                    closeAlertFunctionMock = spyOn(alertFeedback, 'closeAlert');
                    model = {
                        inputParams: {
                            alert: {
                                id: 'alertId'
                            }
                        },
                        closeAlertStatus: 'closeAlertStatus'
                    };

                });

                it('should invoke alertFeedback.closeAlert with alertId + model.closeAlertStatus', function () {

                    alertFeedback._approveAlertCloseCallback(model);
                    expect(closeAlertFunctionMock)
                        .toHaveBeenCalledWith(model.inputParams.alert.id,
                        model.closeAlertStatus);
                });

            });

            describe('_approveAlertReopenCallback', function () {
                var openAlertFunctionMock;
                var model;
                beforeEach(function () {
                    openAlertFunctionMock = spyOn(alertFeedback, 'openAlert');
                    model = {
                        inputParams: {
                            alert: {
                                id: 'alertId'
                            }
                        }
                    };

                });

                it('should invoke alertFeedback.openAlert with alertId + "none"', function () {

                    alertFeedback._approveAlertReopenCallback(model);
                    expect(openAlertFunctionMock)
                        .toHaveBeenCalledWith(model.inputParams.alert.id,
                        'none');
                });

            });

        });

        describe('Public methods', function () {

            describe('setAlertStatus', function () {

                var assertIsObjectMock, assertIsStringMock;
                var patchMock;
                var isValueAllowedMock;

                var config;

                beforeEach(function () {
                    assertIsObjectMock = spyOn(assert, 'isObject');
                    assertIsStringMock = spyOn(assert, 'isString');
                    isValueAllowedMock = spyOn(alertFeedback, '_isValueAllowed').and.callThrough();
                    patchMock = spyOn($http, 'patch');

                    config = {
                        alertId: 'someAlertId',
                        alertStatus: 'open',
                        alertFeedback: 'approved'
                    };
                });

                // Validations

                it('should invoke assert.isObject with config object, "config", ' +
                    'errMsg', function () {
                    alertFeedback.setAlertStatus(config);
                    expect(assertIsObjectMock)
                        .toHaveBeenCalledWith(config, 'config',
                        'alertFeedback.service: setAlertStatus: ');
                });

                it('should invoke assert.isString with config.alertId, "config.alertId", ' +
                    'errMsg', function () {
                    alertFeedback.setAlertStatus(config);
                    expect(assertIsStringMock)
                        .toHaveBeenCalledWith(config.alertId, 'config.alertId',
                        'alertFeedback.service: setAlertStatus: ');
                });

                it('should invoke _isValueAllowed with enumAlertStatus and config.alertStatus ' +
                    'if config.alertStatus is defined', function () {
                    alertFeedback.setAlertStatus(config);
                    expect(isValueAllowedMock)
                        .toHaveBeenCalledWith(alertFeedback.enumAlertStatus, config.alertStatus);
                });

                it('should not invoke _isValueAllowed if config.alertStatus ' +
                    'is not defined', function () {
                    delete config.alertStatus;

                    alertFeedback.setAlertStatus(config);
                    expect(isValueAllowedMock)
                        .not
                        .toHaveBeenCalledWith(alertFeedback.enumAlertStatus, config.alertStatus);
                });

                it('should invoke _isValueAllowed with enumAlertStatus and config.alertStatus ' +
                    'if config.alertStatus is defined', function () {
                    alertFeedback.setAlertStatus(config);
                    expect(isValueAllowedMock)
                        .toHaveBeenCalledWith(alertFeedback.alertFeedbackMap,
                        config.alertFeedback);
                });

                it('should not invoke _isValueAllowed if config.alertStatus ' +
                    'is not defined', function () {
                    delete config.alertFeedback;

                    alertFeedback.setAlertStatus(config);
                    expect(isValueAllowedMock)
                        .not
                        .toHaveBeenCalledWith(alertFeedback.enumAlertFeedback, config.alertFeedback);
                });

                it('should invoke $http.patch with desired url and desired body', function () {
                    var expectedBody = {
                        status: 'open',
                        feedback: 'approved'
                    };

                    var expectedUrl = BASE_URL + '/alerts/someAlertId';

                    alertFeedback.setAlertStatus(config);
                    expect($http.patch.calls.allArgs()[0][0]).toBe(expectedUrl);
                    expect($http.patch.calls.allArgs()[0][1]).toEqual(expectedBody);
                });

                it('should return the result of $http.patch', function () {
                    patchMock.and.returnValue('someValue');
                    expect(alertFeedback.setAlertStatus(config)).toBe('someValue');
                });

            });

            describe('openAlert', function () {

                var setAlertStatusMock;

                var config;

                beforeEach(function () {
                    setAlertStatusMock = spyOn(alertFeedback, 'setAlertStatus');

                    config = {
                        alertId: 'someAlertId',
                        alertFeedback: 'approved'
                    };
                });
                it('should invoke setAlertStatus with config and errMsg', function () {
                    var alertId = config.alertId;
                    var afb = config.alertFeedback;

                    var expectedConfig = {
                        alertId: alertId,
                        alertStatus: alertFeedback.enumAlertStatus.OPEN,
                        alertFeedback: afb
                    };

                    var expectedErrMsg = alertFeedback._errMsg + 'openAlert: ';

                    alertFeedback.openAlert(alertId, afb);
                    expect(setAlertStatusMock.calls.allArgs()[0][0])
                        .toEqual(expectedConfig);
                    expect(setAlertStatusMock.calls.allArgs()[0][1])
                        .toEqual(expectedErrMsg);
                });

                it('should return the result of setAlertStatus', function () {
                    setAlertStatusMock.and.returnValue('someValue');
                    expect(alertFeedback.openAlert()).toBe('someValue');
                });
            });

            describe('closeAlert', function () {

                var setAlertStatusMock;

                var config;

                beforeEach(function () {
                    setAlertStatusMock = spyOn(alertFeedback, 'setAlertStatus');

                    config = {
                        alertId: 'someAlertId',
                        alertFeedback: 'approved'
                    };
                });
                it('should invoke setAlertStatus with config and errMsg', function () {
                    var alertId = config.alertId;
                    var afb = config.alertFeedback;

                    var expectedConfig = {
                        alertId: alertId,
                        alertStatus: alertFeedback.enumAlertStatus.CLOSED,
                        alertFeedback: afb
                    };

                    var expectedErrMsg = alertFeedback._errMsg + 'closeAlert: ';

                    alertFeedback.closeAlert(alertId, afb);
                    expect(setAlertStatusMock.calls.allArgs()[0][0])
                        .toEqual(expectedConfig);
                    expect(setAlertStatusMock.calls.allArgs()[0][1])
                        .toEqual(expectedErrMsg);
                });

                it('should return the result of setAlertStatus', function () {
                    setAlertStatusMock.and.returnValue('someValue');
                    expect(alertFeedback.closeAlert()).toBe('someValue');
                });
            });

            describe('handleCloseAlertPopupFlow', function () {

                var alert;
                beforeEach(function () {

                    alert = {
                        id: 'id',
                        name: 'name'
                    };

                });
                it('should invoke _verifyAlert', function () {
                    var verifyAlertMock = spyOn(alertFeedback, '_verifyAlert');
                    alertFeedback.handleCloseAlertPopupFlow(alert);
                    expect(verifyAlertMock).toHaveBeenCalledWith(alert);

                });

                it('should invoke _openPopup', function () {
                    var openPopupStatusMock = spyOn(alertFeedback, '_openPopup').and.returnValue({
                        result: {
                            then: function () {

                            }
                        }
                    });
                    alertFeedback.handleCloseAlertPopupFlow(alert);
                    expect(openPopupStatusMock).toHaveBeenCalled();

                });
            });

            describe('handleOpenAlertPopupFlow', function () {

                var alert;

                beforeEach(function () {

                    alert = {
                        id: 'id',
                        name: 'name'
                    };

                });
                it('should invoke _verifyAlert', function () {
                    var verifyAlertMock = spyOn(alertFeedback, '_verifyAlert');
                    alertFeedback.handleOpenAlertPopupFlow(alert);
                    expect(verifyAlertMock).toHaveBeenCalledWith(alert);

                });

                it('should invoke _openPopup', function () {
                    var openPopupStatusMock = spyOn(alertFeedback, '_openPopup').and.returnValue({
                        result: {
                            then: function () {

                            }
                        }
                    });
                    alertFeedback.handleOpenAlertPopupFlow(alert);
                    expect(openPopupStatusMock).toHaveBeenCalled();

                });
            });

            describe('showResponseSuccessPopup', function () {

                it('should invoke assert.isString with "newAlertStatus","Alert status", "must be a string", ' +
                    'false & false', function () {
                    var isStringMock = spyOn(assert, 'isString');
                    alertFeedback.showResponseSuccessPopup("open", "some message");
                    expect(isStringMock).toHaveBeenCalledWith("open", 'Alert status', 'must be a string', false, false);

                });

                it('should invoke alertFeedback._isValueAllowed with "alertFeedback.enumAlertStatus" and "open"',
                    function () {
                        var _isValueAllowedMock = spyOn(alertFeedback, '_isValueAllowed').and.returnValue(true);
                        alertFeedback.showResponseSuccessPopup("closed", "some message");
                        expect(_isValueAllowedMock).toHaveBeenCalledWith(alertFeedback.enumAlertStatus, "closed");

                    });

                it('should invoke _openPopup', function () {
                    var openPopupStatusMock = spyOn(alertFeedback, '_openPopup').and.returnValue({
                        result: {
                            then: function () {

                            }
                        }
                    });
                    alertFeedback.showResponseSuccessPopup("closed", "some message");
                    expect(openPopupStatusMock).toHaveBeenCalled();

                });
            });

            describe('showResponseFailurePopup', function () {

                it('should invoke assert.isString with "newAlertStatus","Alert status", "must be a string", ' +
                    'false & false', function () {
                    var isStringMock = spyOn(assert, 'isString');
                    alertFeedback.showResponseFailurePopup("open");
                    expect(isStringMock).toHaveBeenCalledWith("open", 'Alert status', 'must be a string', false, false);

                });

                it('should invoke _openPopup', function () {
                    var openPopupStatusMock = spyOn(alertFeedback, '_openPopup').and.returnValue({
                        result: {
                            then: function () {

                            }
                        }
                    });
                    alertFeedback.showResponseFailurePopup("closed");
                    expect(openPopupStatusMock).toHaveBeenCalled();

                });
            });

        });
    });
}());
