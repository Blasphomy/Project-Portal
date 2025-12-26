import React from 'react';

const StudyMaterial = ({ material }) => {
  return (
    <div>
      <h2>Study Material</h2>
      <p>{material || "Study material will be displayed here."}</p>
    </div>
  );
};

export default StudyMaterial;
