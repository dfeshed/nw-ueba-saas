import { lookup } from 'ember-dependency-lookup';
import RSVP from 'rsvp';

import * as ACTION_TYPES from './types';
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { validateQueries } from './fetch/query-validation';
import {
  pillAsString,
  createPillPositionMap,
  validatablePillsWithPositions
} from 'investigate-events/actions/pill-validation-utils';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';
import { COMPLEX_FILTER } from 'investigate-events/constants/pill';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';


// Exporting it for testing purposes
export const _validateDispatch = (pillsData, markAll = false) => {
  return {
    type: ACTION_TYPES.BATCH_VALIDATE_GUIDED_PILL,
    payload: {
      pillsData,
      markAll
    }
  };
};

/**
 * Updates the validation flag for the pills that were passed into this.
 * If we need to update all, send out an empty array. Reducer will make sure to
 * update all if array is empty.
 */
export const _validationFlagUpdate = (pillsData, validationFlag, updateAll = false) => {
  let positionArray;
  if (updateAll) {
    positionArray = [];
  } else {
    positionArray = pillsData.map(({ position }) => position);
  }
  return {
    type: ACTION_TYPES.VALIDATION_IN_PROGRESS,
    payload: {
      validationFlag,
      positionArray
    }
  };
};

/**
 * Calls out validate queries API. The first time it is called initialCall is true, which
 * stringifies the pills, ANDs them together and sends out validation call.
 * If the response is not empty (failed!), we construct an array of pills and send out the
 * whole array for validation with initialCall false.
 * Responses only include the ones that have failed server side.
 */
const _recursiveServerValidation = (pillsToBeValidated, positionMap, initialCall = false) => {
  return (dispatch, getState) => {

    const { serviceId } = getState().investigate.queryNode;
    const serverPills = [];
    if (initialCall) {
      // create one string with all validatable pills ANDed together
      const pillString = pillsToBeValidated
        .map((pill) => pillAsString(pill))
        .filter((p) => !!p)
        .join(' AND ');
      // ENCODE
      serverPills.push(encodeURIComponent(pillString));
    } else {
      // each pill should have indiviual entry in array
      pillsToBeValidated.forEach((pill) => {
        // ENCODE
        serverPills.push(encodeURIComponent(pillAsString(pill)));
      });
    }

    validateQueries(serviceId, serverPills)
      .then((response) => {
        const { data } = response;
        const map = new Map(Object.entries(data));
        if (initialCall && map.size > 0) {
          // call with full string failed with an error, so we have to go back
          // with individual pills to get pill specific errors
          dispatch(_recursiveServerValidation(pillsToBeValidated, positionMap));
        } else if (map.size > 0) {
          // dispatch errors into state
          const validatedPillsWithPositions = [];
          map.forEach((value, key) => {
            // NEED TO DECODE BEFORE USING THAT KEY
            const decodedKey = decodeURIComponent(key);
            const pill = pillsToBeValidated.find((p) => pillAsString(p) === decodedKey);
            // fetch all positions for a pill (could have duplicates) and for each position
            // create a pill with validation errors and its pill properties
            const allSimilarPills = positionMap.get(pillAsString(pill)).map((position) => {
              return {
                pillData: {
                  ...pill,
                  isInvalid: true,
                  validationError: {
                    message: value
                  }
                },
                position
              };
            });
            // add allSimilarPills to validatedPillsWithPositions
            validatedPillsWithPositions.push(...allSimilarPills);
          });

          // dispatch validatedPillsWithPositions
          dispatch(_validateDispatch(validatedPillsWithPositions, true));
        } else if (map.size === 0) {
          // no invalid queries found. Update all validationInProgress flags as false
          dispatch(_validationFlagUpdate([], false, true));
        }
      })
      .catch((error) => {
        handleInvestigateErrorCode(error, 'SERVER_VALIDATION_ERROR');
        dispatch(_validationFlagUpdate([], false, true));
      });
  };
};

/**
 * In order to reduce the server API load, if there are 5 or more pills, we
 * stringify them together and send out for validation.
 * This method when called with pills, first runs them through client side
 * validation. If any one of them failed client side, it updates state with the error.
 * For the rest of pills that passed client side, dispatches recursiveServerSideValidation.
 * If there is just one pill left after client side was completed, we mark initialCall to
 * false in order to NOT make the second recursive call.
 */
export const batchValidation = (pillsData, initialPosition, isFromParser) => {
  return (dispatch, getState) => {

    // Pick out pills that can be validated
    const pillsToBeValidated = validatablePillsWithPositions(pillsData, initialPosition);

    if (pillsToBeValidated.length > 0) {
      // If there are any pills that can be validated
      const { language, aliases } = languageAndAliasesForParser(getState());

      // dispatch validation in progress flag indicating we will begin validation
      dispatch(_validationFlagUpdate(pillsToBeValidated, true));

      // Process client side for validatable pills
      const invalidClientSidePills = pillsToBeValidated
        .map(({ pillData, position }) => _clientValidate(pillData, position, isFromParser, language, aliases, true))
        .filter((p) => !!p);

      // make sure to get correct positions for invalid client pills
      // send invalid client pills to state
      const failedClientSet = new Set();
      if (invalidClientSidePills.length > 0) {
        dispatch(_validateDispatch(invalidClientSidePills));
        invalidClientSidePills.forEach((p) => failedClientSet.add(p.pillData));
      }

      // Create a pill set that will be used to find difference
      const originalPillsSet = new Set();
      pillsToBeValidated.forEach((p) => originalPillsSet.add(p.pillData));
      // Negate client invalid pills from original pill set
      const validClientSidePills = originalPillsSet.difference(failedClientSet);

      // remaining pills - send for server validation
      if (validClientSidePills.size > 0) {
        // Create a position map to keep track of pills
        const positionMap = createPillPositionMap(pillsData, initialPosition);
        dispatch(_recursiveServerValidation(validClientSidePills, positionMap, validClientSidePills.size > 1));
      }
    }
  };

};

const _clientValidate = (pillData, position, isFromParser, language, aliases, isBatch = false) => {
  const { type } = pillData;
  if (!isFromParser && type !== COMPLEX_FILTER) {
    // If not from parser, no validation has been performed yet. Re-get pillData
    // by putting through parser to do client side validation.
    const { meta, operator, value } = pillData;
    const pills = transformTextToPillData(`${meta || ''} ${operator || ''} ${value || ''}`.trim(), { language, aliases, returnMany: true });
    if (pills.length > 1) {
      const i18n = lookup('service:i18n');
      pillData.isInvalid = true;
      pillData.validationError = i18n.t('queryBuilder.validationMessages.tooManyPills');
    } else {
      pillData = pills[0];
    }
  }

  if (!isBatch) {
    return pillData;
  }

  // For batch pills sent for client side, we only ever need invalid pills
  const { isInvalid } = pillData;
  if (isInvalid && type !== COMPLEX_FILTER) {
    return {
      pillData,
      position
    };
  }
};

/**
 * Client side validation. Parser handles most validation, but if the pill has
 * not been through the parser already, do so.
 * 1. If the pill returned is a normal query pill & is not marked invalid,
 *    send for server side validation.
 * 2. If the pill is complex, send for server side validation.
 * 3. If the pill was marked invalid by the parser, no action is needed. The
 *    invalid pill is already in state with the correct error message.
 * @private
 */
export const clientSideValidation = ({ pillData: pill, position, isFromParser = false }) => {
  return (dispatch, getState) => {
    const { language, aliases } = languageAndAliasesForParser(getState());
    const pillData = _clientValidate(pill, position, isFromParser, language, aliases);
    const { isInvalid, type } = pillData;

    if (isInvalid && !isFromParser && type !== COMPLEX_FILTER) {
      // If the pill is marked invalid but was not from the parser, dispatch an action now to mark it invalid.
      // Otherwise, it was already marked invalid.
      dispatch({
        type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
        promise: RSVP.Promise.reject({ meta: pillData.validationError }),
        meta: {
          position, // position is needed to update pill in reducer
          isServerSide: false // sets `isValidationInProgress = true` while the req is being processed
        }
      });
    } else if (!isInvalid || type === COMPLEX_FILTER) {
      // This catches complex pills, and pills that passed client side validation.
      // We still want to perform server side validation on those.
      dispatch(_serverSideValidation(pillData, position));
    }
    // The only pills left once we get here are invalid pills that were invalid
    // before being sent to this method. Those are already marked invalid in
    // state, so no work needs to be done.
  };
};

export const _serverSideValidation = (pillData, position) => {
  return (dispatch, getState) => {
    const { meta, operator, value, complexFilterText } = pillData;
    let stringifiedPill;
    // create stringified pill data, or just use the what was entered for
    // complex filters
    if (meta && operator) {
      stringifiedPill = `${meta || ''} ${operator || ''} ${value || ''}`.trim();
    } else {
      stringifiedPill = complexFilterText;
    }
    // encode the string and pull out the service id
    const encodedPill = encodeURIComponent(stringifiedPill);
    const { serviceId } = getState().investigate.queryNode;
    dispatch({
      type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
      promise: validateQueries(serviceId, [encodedPill]),
      meta: {
        position, // position is needed to update pill in reducer
        isServerSide: true // sets `isValidationInProgress = false` after the req was processed
      }
    });
  };
};