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
        isTransactionUnderway: false
      }
    }
  }
};
