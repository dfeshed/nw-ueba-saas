const loadedFiles = [
  {
    field: 'filename.dst'
  },
  {
    label: 'recon.textView.endpointDetail.loadedInto',
    field: 'filename.src'
  }
];

export default {
  File: {
    fields: [
      {
        field: 'filename'
      },
      {
        label: 'recon.textView.endpointDetail.presentIn',
        field: 'directory'
      }
    ]
  },
  'System Event': {
    fields: [
      {
        label: 'recon.textView.endpointDetail.triggeredEventTypeIs',
        field: 'event.type'
      }
    ]
  },
  'Network Event': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        label: 'recon.textView.endpointDetail.madeConnectionTo',
        field: 'ip.dst'
      },
      {
        label: 'recon.textView.endpointDetail.madeConnectionTo',
        field: 'ipv6.dst'
      },
      {
        label: 'recon.textView.endpointDetail.resolvedTo',
        field: 'domain.dst'
      },
      {
        label: 'recon.textView.endpointDetail.from',
        field: 'ip.src'
      },
      {
        label: 'recon.textView.endpointDetail.from',
        field: 'ipv6.src'
      }
    ]
  },
  'Registry Event': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        field: 'action'
      },
      {
        field: 'registry.key'
      }
    ]
  },
  'Machine': {
    fields: [
      {
        message: 'recon.textView.endpointDetail.machine'
      }
    ]
  },
  'Process Event': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        label: 'recon.textView.endpointDetail.performed',
        field: 'action'
      },
      {
        icon: 'arrow-right-1',
        field: 'filename.dst'
      }
    ]
  },
  'File Event': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        label: 'recon.textView.endpointDetail.performed',
        field: 'action'
      },
      {
        label: 'recon.textView.endpointDetail.on',
        field: 'filename.dst'
      }
    ]
  },
  Autorun: {
    fields: [
      {
        field: 'filename',
        postfixLabel: 'recon.textView.endpointDetail.autorun'
      }
    ]
  },
  Service: {
    fields: [
      {
        field: 'filename'
      },
      {
        label: 'recon.textView.endpointDetail.runningAsService',
        field: 'service.name'
      }
    ]
  },
  Dll: {
    fields: loadedFiles
  },
  Task: {
    fields: [
      {
        field: 'filename'
      },
      {
        label: 'recon.textView.endpointDetail.runningAsTask',
        field: 'task.name'
      }
    ]
  },
  Process: {
    fields: [
      {
        field: 'filename.dst'
      },
      {
        label: 'recon.textView.endpointDetail.launchedBy',
        field: 'filename.src'
      }
    ]
  },
  'Console Event': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        label: 'recon.textView.endpointDetail.ran',
        field: 'param.src'
      }
    ]
  },
  'Kernel Hook': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        label: 'recon.textView.endpointDetail.hookedFunctionIn',
        field: 'filename.dst'
      }
    ]
  },
  'Image Hook': {
    fields: [
      {
        field: 'filename.src'
      },
      {
        label: 'recon.textView.endpointDetail.hooked',
        field: 'filename.dst'
      },
      {
        label: 'recon.textView.endpointDetail.colon',
        field: 'function'
      },
      {
        label: 'recon.textView.endpointDetail.loadedIn',
        field: 'filename'
      }
    ]
  },
  'Dylib': {
    fields: loadedFiles
  },
  'Loaded Library': {
    fields: loadedFiles
  },
  'Daemon': {
    fields: [
      {
        field: 'filename',
        postfixLabel: 'recon.textView.endpointDetail.daemon'
      }
    ]
  },
  'Initd': {
    fields: [
      {
        field: 'filename',
        postfixLabel: 'recon.textView.endpointDetail.initd'
      }
    ]
  },
  'Systemd': {
    fields: [
      {
        field: 'filename',
        postfixLabel: 'recon.textView.endpointDetail.systemd'
      }
    ]
  },
  'Cron': {
    fields: [
      {
        field: 'filename',
        postfixLabel: 'recon.textView.endpointDetail.cron'
      }
    ]
  }
};