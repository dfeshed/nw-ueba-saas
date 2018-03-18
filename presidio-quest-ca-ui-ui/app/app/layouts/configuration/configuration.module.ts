module Fortscale.layouts.configuration {
    'use strict';
    export const CONFIG_FORM_STATE_NAME = 'configuration.configForm';
    export const NAV_BAR_ELEMENT_SELECTOR = '.menu-pane--menu-container';
    export const OPENED_CLASS_NAME = 'opened';
    export const CLOSED_CLASS_NAME = 'closed';

    angular.module('Fortscale.layouts.configuration', [
        'Fortscale.shared.services.tagsUtils',
        'Fortscale.shared.services.stringUtils'
    ]);

}

