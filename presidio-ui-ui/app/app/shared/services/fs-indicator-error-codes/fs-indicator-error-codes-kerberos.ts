module Fortscale {
    (function () {
        'use strict';

        var KerberosErrorCodes: any = {
            0x1: {
                "description": "Client's entry in KDC database has expired",
                "displayMessage": "Client's entry in KDC database has expired"
            },
            0x2: {
                "description": "Server's entry in KDC database has expired",
                "displayMessage": "Server's entry in KDC database has expired"
            },
            0x3: {
                "description": "Requested Kerberos version number not supported",
                "displayMessage": "Requested Kerberos version number not supported"
            },
            0x4: {
                "description": "Client's key encrypted in old master key",
                "displayMessage": "Client's key encrypted in old master key"
            },
            0x5: {
                "description": "Server's key encrypted in old master key",
                "displayMessage": "Server's key encrypted in old master key"
            },
            0x6: {
                "description": "Client not found in Kerberos database",
                "displayMessage": "Client not found in Kerberos database"
            },
            0x7: {
                "description": "Server not found in Kerberos database",
                "displayMessage": "Server not found in Kerberos database"
            },
            0x8: {
                "description": "Multiple principal entries in KDC database",
                "displayMessage": "Multiple principal entries in KDC database"
            },
            0x9: {
                "description": "The client or server has a null key (master key)",
                "displayMessage": "The client or server has a null key (master key)"
            },
            0xA: {
                "description": "Ticket (TGT) not eligible for postdating",
                "displayMessage": "Ticket (TGT) not eligible for postdating"
            },
            0xB: {
                "description": "Requested start time is later than end time",
                "displayMessage": "Requested start time is later than end time"
            },
            0xC: {
                "description": "KDC policy rejects request",
                "displayMessage": "KDC policy rejects request"
            },
            0xD: {
                "description": "KDC cannot accommodate requested option",
                "displayMessage": "KDC cannot accommodate requested option"
            },
            0xE: {
                "description": "KDC has no support for encryption type",
                "displayMessage": "KDC has no support for encryption type"
            },
            0xF: {
                "description": "KDC has no support for checksum type",
                "displayMessage": "KDC has no support for checksum type"
            },
            0x10: {
                "description": "KDC has no support for PADATA type (pre-authentication data)",
                "displayMessage": "KDC has no support for PADATA type"
            },
            0x11: {
                "description": "KDC has no support for transited type",
                "displayMessage": "KDC has no support for transited type"
            },
            0x12: {
                "description": "Client’s credentials have been revoked",
                "displayMessage": "Client’s credentials have been revoked"
            },
            0x13: {
                "description": "Credentials for server have been revoked",
                "displayMessage": "Credentials for server have been revoked"
            },
            0x14: {
                "description": "TGT has been revoked",
                "displayMessage": "TGT has been revoked"
            },
            0x15: {
                "description": "Client not yet valid—try again later",
                "displayMessage": "Client not yet valid"
            },
            0x16: {
                "description": "Server not yet valid—try again later",
                "displayMessage": "Server not yet valid"
            },
            0x17: {
                "description": "Password has expired—change password to reset",
                "displayMessage": "Password has expired"
            },
            0x18: {
                "description": "Pre-authentication information was invalid",
                "displayMessage": "Pre-authentication information was invalid"
            },
            0x19: {
                "description": "Additional preauthentication required",
                "displayMessage": "Additional preauthentication required"
            },
            0x1A: {
                "description": "KDC does not know about the requested server",
                "displayMessage": "KDC does not know about the requested server"
            },
            0x1B: {
                "description": "KDC is unavailable",
                "displayMessage": "KDC is unavailable"
            },
            0x1F: {
                "description": "Integrity check on decrypted field failed",
                "displayMessage": "Integrity check on decrypted field failed"
            },
            0x20: {
                "description": "The ticket has expired",
                "displayMessage": "The ticket has expired"
            },
            0x21: {
                "description": "The ticket is not yet valid",
                "displayMessage": "The ticket is not yet valid"
            },
            0x22: {
                "description": "The request is a replay",
                "displayMessage": "The request is a replay"
            },
            0x23: {
                "description": "The ticket is not for us",
                "displayMessage": "The ticket is not for us"
            },
            0x24: {
                "description": "The ticket and authenticator do not match",
                "displayMessage": "The ticket and authenticator do not match"
            },
            0x25: {
                "description": "The clock skew is too great",
                "displayMessage": "The clock skew is too great"
            },
            0x26: {
                "description": "Network address in network layer header doesn't match address inside ticket",
                "displayMessage": "Incorrect network address"
            },
            0x27: {
                "description": "Protocol version numbers don't match (PVNO)",
                "displayMessage": "Protocol version numbers don't match"
            },
            0x28: {
                "description": "Message type is unsupported",
                "displayMessage": "Message type is unsupported"
            },
            0x29: {
                "description": "Message stream modified and checksum didn't match",
                "displayMessage": "Message stream modified and checksum didn't match"
            },
            0x2A: {
                "description": "Message out of order (possible tampering)",
                "displayMessage": "Message out of order (possible tampering)"
            },
            0x2C: {
                "description": "Specified version of key is not available",
                "displayMessage": "Specified version of key is not available"
            },
            0x2D: {
                "description": "Service key not available",
                "displayMessage": "Service key not available"
            },
            0x2E: {
                "description": "Mutual authentication failed",
                "displayMessage": "Mutual authentication failed"
            },
            0x2F: {
                "description": "Incorrect message direction",
                "displayMessage": "Incorrect message direction"
            },
            0x30: {
                "description": "Alternative authentication method required",
                "displayMessage": "Alternative authentication method required"
            },
            0x31: {
                "description": "Incorrect sequence number in message",
                "displayMessage": "Incorrect sequence number in message"
            },
            0x32: {
                "description": "Inappropriate type of checksum in message (checksum may be unsupported)",
                "displayMessage": "Inappropriate type of checksum in message"
            },
            0x33: {
                "description": "Desired path is unreachable",
                "displayMessage": "Desired path is unreachable"
            },
            0x34: {
                "description": "Too much data",
                "displayMessage": "Too much data"
            },
            0x3C: {
                "description": "Generic error; the description is in the e-data field",
                "displayMessage": "Generic error; the description is in the e-data field"
            },
            0x3D: {
                "description": "Field is too long for this implementation",
                "displayMessage": "Field is too long for this implementation"
            },
            0x3E: {
                "description": "The client trust failed or is not implemented",
                "displayMessage": "The client trust failed or is not implemented"
            },
            0x3F: {
                "description": "The KDC server trust failed or could not be verified",
                "displayMessage": "The KDC server trust failed or could not be verified"
            },
            0x40: {
                "description": "The signature is invalid",
                "displayMessage": "The signature is invalid"
            },
            0x41: {
                "description": "A higher encryption level is needed",
                "displayMessage": "A higher encryption level is needed"
            },
            0x42: {
                "description": "User-to-user authorization is required",
                "displayMessage": "User-to-user authorization is required"
            },
            0x43: {
                "description": "No TGT was presented or available",
                "displayMessage": "No TGT was presented or available"
            },
            0x44: {
                "description": "Incorrect domain or principal",
                "displayMessage": "Incorrect domain or principal"
            }
        };

        angular.module('Fortscale.shared.services.fsIndicatorErrorCodes')
        .run(['fsIndicatorErrorCodes',
            (fsIndicatorErrorCodes: IIndicatorErrorCodesService) => {
                fsIndicatorErrorCodes.addErrorObject('kerberos_logins', KerberosErrorCodes);
                fsIndicatorErrorCodes.addErrorObject('kerberos_tgt', KerberosErrorCodes);
            }]);
    }());
}
