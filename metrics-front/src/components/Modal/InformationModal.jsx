import React, { useState, useEffect } from "react";
import { Modal, Button } from "react-bootstrap";
import DistributedTimelineChart from "../Chart/DistributedTimelineChart.jsx";

const InformationModal = (props) => {
  const [content, setContent] = useState([]);

  useEffect(() => {
    setContent(props.content);
  }, [props.content]);

  return (
    <Modal
      show={props.show}
      onHide={props.handleClose}
      size="xl"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title>Metric details</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <DistributedTimelineChart height={350} content={content} />
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={props.handleClose}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default InformationModal;
