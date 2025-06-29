import pandas as pd
import numpy as np
from sdv.metadata import SingleTableMetadata
from sdv.single_table import CTGANSynthesizer

df = pd.read_csv("data/winequality-combined.csv")

print("Nulos por columna:\n", df.isnull().sum())

df.dropna(inplace=True)

metadata = SingleTableMetadata()
metadata.detect_from_dataframe(data=df)
metadata.update_column(column_name='type', sdtype='categorical')

synthesizer = CTGANSynthesizer(metadata, enforce_rounding=True, epochs=2000, verbose=True)
synthesizer.fit(df)

chunk_sizes = [10_000, 50_000, 100_000]
base_filename = "data/winequality-synthetic-"

for i, size in enumerate(chunk_sizes, start=1):
    with open(f"{base_filename}{i}.csv", "w") as f:
        total_generated = 0
        while total_generated < size:
            remaining = size - total_generated
            sample_size = min(remaining, 100_000)
            chunk = synthesizer.sample(sample_size)
            chunk.dropna(inplace=True)
            while chunk.empty:
                chunk = synthesizer.sample(sample_size)
                chunk.dropna(inplace=True)
            chunk.to_csv(f, index=False, header=(total_generated == 0), mode='a')
            total_generated += len(chunk)