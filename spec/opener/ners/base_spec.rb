require 'spec_helper'

describe Opener::Ners::Base do
  before do
    @ner = described_class.new
  end

  describe '#language_from_kaf' do
    describe 'using a supported language' do
      it 'returns the language as a String' do
        @ner.language_from_kaf('<KAF xml:lang="en" />').should == 'en'
      end
    end

    describe 'using a language with an invalid format' do
      it 'raises a UnsupportedLanguageError' do
        expect { @ner.language_from_kaf('<KAF xml:lang="../../foo" />') }
          .to raise_error(Opener::Core::UnsupportedLanguageError)
      end
    end

    describe 'without a language' do
      it 'raises a UnsupportedLanguageError' do
        expect { @ner.language_from_kaf('<KAF xml:lang="" />') }
          .to raise_error(Opener::Core::UnsupportedLanguageError)
      end
    end
  end
end
