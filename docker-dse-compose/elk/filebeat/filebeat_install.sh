#!/bin/bash
dpkg -i ~/filebeat/filebeat-*.deb

cat > /etc/filebeat/filebeat.yml <<EOF
output.elasticsearch:
    enabled: true
    hosts: ["elasticsearch:9200"]
setup.kibana:
  host: "kibana:5601"
#setup.dashboards.enabled: true
filebeat.inputs:
    - type: log
      paths:
        - "/var/log/cassandra/system.log*"
      document_type: cassandra_system_logs
      exclude_files: ['\.zip$']
      multiline.pattern: '^TRACE|DEBUG|WARN|INFO|ERROR'
      multiline.negate: true
      multiline.match: after
processors:
    - dissect:
            target_prefix: "cassandra" 
            tokenizer: "%{level} [%{thread}] %{date} %{time} %{service} %{class}:%{line} - %{msg}"
EOF

service filebeat start
