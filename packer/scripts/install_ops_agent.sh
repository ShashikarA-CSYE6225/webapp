#!/bin/bash
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install
sudo touch /var/log/webapp.log
sudo chown -R csye6225:csye6225 /var/log/webapp.log

# Define the YAML content to append
yaml_content=$(cat <<EOF
logging:
  receivers:
    my-app-receiver:
      type: files
      include_paths:
        - /var/log/webapp.log
      record_log_file_path: true
  processors:
    my-app-processor:
      type: parse_json
      time_key: time
      time_format: "%Y-%m-%dT%H:%M:%S.%L%Z"
    extract_structure:
          type: parse_regex
          field: message
          regex: "^(?<time>[\d-]*T[\d:.Z]*) (?<severity>[^ ]*) (?<file>[^ :]*):(?<line>[\d]*) - (?<message>(.|\\n)*)$"
          time_key: time
          time_format: "%Y-%m-%dT%H:%M:%S.%L"
    move_severity:
          type: modify_fields
          fields:
            severity:
              move_from: jsonPayload.severity
  service:
    pipelines:
      default_pipeline:
        receivers: [my-app-receiver]
        processors: [my-app-processor, move_severity]
EOF
)

# Define the path to the config.yaml file
config_file="/etc/google-cloud-ops-agent/config.yaml"

# Append YAML content to the config file
echo "$yaml_content" | sudo tee -a "$config_file" > /dev/null
echo "YAML content appended to $config_file"