const prefix = 'configure.incidentRules.riskScoring.';
export const t = (context, key) => {
  const i18n = context.owner.lookup('service:i18n');
  return i18n.t(`${prefix}${key}`);
};

export const labels = (context, name) => {
  switch (name) {
    case 'updateFailure': {
      return t(context, `actionMessages.${name}`);
    }
    case 'updateSuccess': {
      return t(context, `actionMessages.${name}`);
    }
    case 'fetchFailure': {
      return t(context, `actionMessages.${name}`);
    }
    case 'hours': {
      return t(context, 'timeUnits.h');
    }
    case 'days': {
      return t(context, 'timeUnits.d');
    }
    default: {
      return t(context, `labels.${name}`);
    }
  }
};
