export const normalizedState = {
  configure: {
    respond: {
      riskScoring: {
        riskScoringSettings: {
          host: {
            threshold: '75',
            timeWindow: '1',
            timeWindowUnit: 'd'
          },
          file: {
            threshold: '80',
            timeWindow: '24',
            timeWindowUnit: 'h'
          }
        },
        riskScoringStatus: 'wait',
        riskScoringExpanded: false,
        isTransactionUnderway: false
      }
    }
  }
};

export const normalizedStateExpanded = {
  ...normalizedState,
  configure: {
    ...normalizedState.configure,
    respond: {
      ...normalizedState.configure.respond,
      riskScoring: {
        ...normalizedState.configure.riskScoring,
        riskScoringSettings: { ...normalizedState.configure.respond.riskScoring.riskScoringSettings },
        riskScoringStatus: 'wait',
        riskScoringExpanded: true,
        isTransactionUnderway: false
      }
    }
  }
};
