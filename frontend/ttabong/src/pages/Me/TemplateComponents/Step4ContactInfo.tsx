import { TemplateFormData } from "@/types/template";
import React from "react";

interface Step4Props {
  templateData: {
    contactName: string;
    contactPhone: {
      areaCode: string;
      middle: string;
      last: string;
    };
  };
  setTemplateData: React.Dispatch<React.SetStateAction<TemplateFormData>>;
}


const Step4ContactInfo: React.FC<Step4Props> = ({ templateData, setTemplateData }) => {
  const areaCodes = ["02", "031", "032", "041", "042", "043", "051", "052", "053", "054", "055", "061", "062", "063", "064", "070", "010"];

  const handleNumberInput = (e: React.ChangeEvent<HTMLInputElement>, field: 'middle' | 'last') => {
    const value = e.target.value.replace(/\D/g, "").slice(0, 4);
    setTemplateData(prev => ({
      ...prev,
      contactPhone: {
        ...prev.contactPhone,
        [field]: value
      }
    }));
  };

  return (
    <div className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">담당자명</label>
        <input
          type="text"
          className="w-full p-2 border rounded-md"
          placeholder="김싸피"
          value={templateData.contactName}
          onChange={(e) => setTemplateData((prev:TemplateFormData) => ({
            ...prev,
            contactName: e.target.value
          }))}
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">담당자 연락처</label>
        <div className="flex gap-2">
          <select
            className="w-1/4 p-2 border rounded-md"
            value={templateData.contactPhone.areaCode}
            onChange={(e) => setTemplateData(prev => ({
              ...prev,
              contactPhone: {
                ...prev.contactPhone,
                areaCode: e.target.value
              }
            }))}
          >
            {areaCodes.map((code) => (
              <option key={code} value={code}>{code}</option>
            ))}
          </select>

          <input
            type="text"
            className="w-1/4 p-2 border rounded-md text-center"
            placeholder="XXXX"
            value={templateData.contactPhone.middle}
            onChange={(e) => handleNumberInput(e, 'middle')}
          />

          <input
            type="text"
            className="w-1/4 p-2 border rounded-md text-center"
            placeholder="XXXX"
            value={templateData.contactPhone.last}
            onChange={(e) => handleNumberInput(e, 'last')}
          />
        </div>
      </div>
    </div>
  );
};

export default Step4ContactInfo;
