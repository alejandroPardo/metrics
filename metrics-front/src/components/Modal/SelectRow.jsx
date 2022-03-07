import React, { useState, useEffect } from "react";
import { Col, Row, Container } from "react-bootstrap";

const SelectRow = (props) => {
  const [selected, setSelected] = useState(props.selected);
  const [content, setContent] = useState({});

  useEffect(() => {
    let auxSelected = props.selected;
    let auxContent = {};
    if (props.selected === 0) {
      auxContent.name = props.content.name;
      auxContent.timestamp = props.content.timestamp;
      auxContent.description = props.content.description;
      auxContent.duration = `${props.content.duration} ms.`;
    } else {
      auxContent.name = props.content.transactions[auxSelected - 1].name;
      auxContent.timestamp =
        props.content.transactions[auxSelected - 1].timestamp;
      auxContent.description = `${
        props.content.transactions[auxSelected - 1].type
      } ${props.content.transactions[auxSelected - 1].value}`;
      auxContent.duration = `${
        props.content.transactions[auxSelected - 1].duration
      } ms.`;
    }
    setSelected(props.selected);
    setContent(auxContent);
  }, [props]);

  return (
    <Container className="modalContent pt-3">
      <h3>Transaction Details</h3>
      <Row className="pt-3">
        <Col>{content.name}</Col>
        <Col>{content.description}</Col>
        <Col>{content.timestamp}</Col>
        <Col>{content.duration}</Col>
      </Row>
    </Container>
  );
};

export default SelectRow;
