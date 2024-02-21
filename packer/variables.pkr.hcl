variable "project_id" {
  description = "The project ID for the Google Cloud project"
  type        = string
}

variable "source_image" {
  description = "The source image to use for building the custom image"
  type        = string
}

variable "image_name" {
  description = "The name of the custom image to be created"
  type        = string
}

variable "zone" {
  description = "The zone in which the instance will be created"
  type        = string
}

variable "disk_size" {
  description = "The size of the disk for the instance"
  type        = number
}

variable "network" {
  description = "The network to use for the instance"
  type        = string
}

variable "communicator" {
  description = "The type of communicator to use"
  type        = string
}

variable "ssh_username" {
  description = "The username for SSH access"
  type        = string
}

variable "ssh_password" {
  description = "The password for SSH access"
  type        = string
}

variable "ssh_timeout" {
  description = "The timeout for SSH connections"
  type        = string
}

variable "credentials_file" {
  type = string
}

variable "service_account_email" {
  type = string
}




