import { TooltipProps } from "recharts";

interface CustomTooltipProp extends TooltipProps<number, string>{}

const CustomTooltip: React.FC<CustomTooltipProp> = ({active, payload, label}) =>{
  if (!active || !payload || payload.length === 0) return null;
  return(
      <div
          style={{
              backgroundColor: '#2C3B49',
              color: '#fff',
              padding: '8px 12px',
              borderRadius: 8,
              boxShadow: '0 0 8px rgba(136, 132, 216, 0.5)',
              fontSize: '0.85rem',
              pointerEvents: 'none',
              minWidth: 140,
          }}
      >
          <div style={{ marginBottom: 6, color: '#8884d8', fontWeight: 600 }}>{label}</div>
          {payload.map((entry, index) => (
              <div key={index} style={{ color: entry.color, marginBottom: 4 }}>
                  {entry.name}: {entry.value}
              </div>
          ))}
      </div>
  )
}

export default CustomTooltip;