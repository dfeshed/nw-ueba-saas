export const normalizedState = {
  configure: {
    respond: {
      riskScoring: {
        riskScoringSettings: {
          host: {
            threshold: '75',
            timeWindow: '1',
            timeWindowUnit: 'd',
            enabled: true
          },
          file: {
            threshold: '80',
            timeWindow: '24',
            timeWindowUnit: 'h',
            enabled: true
          }
        },
        riskScoringStatus: 'completed',
        riskScoringExpanded: false,
        isTransactionUnderway: false
      }
    }
  }
};

export const getRiskScoringSettings = function() {
  const {
    configure: {
      respond: {
        riskScoring: {
          riskScoringSettings
        }
      }
    }
  } = normalizedState;

  return {
    ...riskScoringSettings
  };
};

export function getNormalizedState({ riskScoringStatus, riskScoringExpanded = true }) {
  return {
    ...normalizedState,
    configure: {
      ...normalizedState.configure,
      respond: {
        ...normalizedState.configure.respond,
        riskScoring: {
          ...normalizedState.configure.riskScoring,
          riskScoringSettings: { ...normalizedState.configure.respond.riskScoring.riskScoringSettings },
          riskScoringStatus,
          riskScoringExpanded,
          isTransactionUnderway: false
        }
      }
    }
  };
}
