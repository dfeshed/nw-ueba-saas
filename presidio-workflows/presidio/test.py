
import json

import pendulum
import pytz

from datetime import datetime, timedelta

from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY
from presidio.utils.services.time_service import floor_time


def main():
    r = {"startInstant": "2019-07-11T00:00:00Z", "entityTypes": ["domain"], "endInstant": "2019-07-12T00:00:00Z",
         "schema": "TLS"}

    b = {"bar": "fd"}
    a = b.copy()
    a.update({"d" : ""})

    print(a)
    print(b)


if __name__ == "__main__":
    main()
