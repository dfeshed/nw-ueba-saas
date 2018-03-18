module Fortscale {
    (function () {
        'use strict';

        var NTLMErrorCodes: any = {
            0: {
                "description": "Success",
                "displayMessage": "Success"
            },
            3221225572: {
                "description": "user name does not exist",
                "displayMessage": "Username doesn't exist"
            },
            3221225578: {
                "description": "user name is correct but the password is wrong",
                "displayMessage": "Wrong password"
            },
            3221226036: {
                "description": "user is currently locked out",
                "displayMessage": "User is locked out"
            },
            3221225586: {
                "description": "account is currently disabled",
                "displayMessage": "Account is disabled"
            },
            3221225583: {
                "description": "user tried to logon outside his day of week or time of day restrictions",
                "displayMessage": "Logon day/time restrictions"
            },
            3221225584: {
                "description": "workstation restriction",
                "displayMessage": "Workstation restriction"
            },
            3221225875: {
                "description": "account expiration",
                "displayMessage": "Account expiration"
            },
            3221225585: {
                "description": "expired password",
                "displayMessage": "Expired password"
            },
            3221226020: {
                "description": "user is required to change password at next logon",
                "displayMessage": "Change password required"
            },
            3221226021: {
                "description": "evidently a bug in Windows and not a risk",
                "displayMessage": "Windows issue"
            }
        };

        angular.module('Fortscale.shared.services.fsIndicatorErrorCodes')
        .run(['fsIndicatorErrorCodes',
            (fsIndicatorErrorCodes: IIndicatorErrorCodesService) => {
                fsIndicatorErrorCodes.addErrorObject('ntlm', NTLMErrorCodes);
            }]);
    }());
}
