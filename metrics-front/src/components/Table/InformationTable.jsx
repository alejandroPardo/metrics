import React, { useState, useEffect } from 'react';
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, { textFilter } from 'react-bootstrap-table2-filter';
import InformationModal from '../Modal/InformationModal.jsx';

const InformationTable = (props) => {
  const [show, setShow] = useState(false);
  const [modalContent, setModalContent] = useState('');
  const hideModal = () => setShow(false);

  const [data, setData] = useState([]);
  const [columns, setColumns] = useState([{}]);

  useEffect(() => {
    setData(props.data);
    setColumns(props.columns);
    console.log(data);
    console.log(columns)
  },[columns, data, props]);

  const getModalData = (transaction_uuid) => {
    return `hola ${transaction_uuid}`;
  }

  const rowEvents = {
    onClick: (e, row, rowIndex) => {
      setModalContent(getModalData(row.transaction_uuid));
      setShow(true);
    }
  };

  return (
      <>
        <BootstrapTable
          striped
          hover
          condensed
          keyField="transactionUuid"
          data={data}
          columns={columns}
          bordered={false}
          filter={filterFactory()}
          rowEvents={rowEvents} 
          noDataIndication="Table is Empty"
        />
        <InformationModal show={show} handleClose={hideModal} content={modalContent} />
      </>
  );
};

export default InformationTable;