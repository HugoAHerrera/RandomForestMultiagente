import pandas as pd
from sdv.metadata import SingleTableMetadata
from sdv.single_table import CTGANSynthesizer

df = pd.read_csv("data/winequality-combined.csv")

metadata = SingleTableMetadata()
metadata.detect_from_dataframe(data=df)
metadata.update_column(column_name='type', sdtype='categorical')

synthesizer = CTGANSynthesizer(metadata, # required
    enforce_rounding=True,
    epochs=2000,
    verbose=True
)
synthesizer.fit(df)

chunk_size = 2_000_000
total_rows = 12_000_000
output_file = "data/winequality-synthetic-small.csv"

with open(output_file, "w") as f:
    for i in range(total_rows // chunk_size):
        print(f"Chunk {i + 1}: {total_rows // chunk_size}...")
        chunk = synthesizer.sample(chunk_size)
        chunk.to_csv(f, index=False, header=(i == 0))

"""
chunk_size = 2_000_000
total_rows = 60_000_000
output_file = "data/winequality-synthetic-plus.csv"

with open(output_file, "w") as f:
    for i in range(total_rows // chunk_size):
        print(f"Chunk {i + 1}: {total_rows // chunk_size}...")
        chunk = synthesizer.sample(chunk_size)
        chunk.to_csv(f, index=False, header=(i == 0))

synthesizer2 = CTGANSynthesizer(metadata, # required
    enforce_rounding=True,
    epochs=2000,
    verbose=True
)
synthesizer2.fit(df)

chunk_size = 2_000_000
total_rows = 280_000_000
output_file = "data/winequality-synthetic-big.csv"

with open(output_file, "w") as f:
    for i in range(total_rows // chunk_size):
        print(f"Chunk {i + 1}: {total_rows // chunk_size}...")
        chunk = synthesizer2.sample(chunk_size)
        chunk.to_csv(f, index=False, header=(i == 0))
"""
"""
reference_data = pd.DataFrame({
    'type': ['white'],
    'quality': [6]
})

generated = synthesizer.sample_remaining_columns(known_columns=reference_data)
print(generated)

"""
"""
import pandas as pd
from sdv.single_table import GaussianCopulaSynthesizer
from sdv.metadata import SingleTableMetadata

df = pd.read_csv("data/winequality-combined.csv")
metadata = SingleTableMetadata()
metadata.detect_from_dataframe(data=df)

metadata.update_column(
    column_name='type',
    sdtype='categorical'
)

model = GaussianCopulaSynthesizer(metadata)
model.fit(df)

synthetic_data = model.sample(num_rows=6000)
synthetic_data.to_csv("data/winequality-synthetic.csv", index=False)

print(synthetic_data.head())
"""
